package arrow.meta.ide.plugins.proofs.annotators.resolution

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.bindingCtx
import arrow.meta.ide.dsl.utils.localQuickFix
import arrow.meta.ide.dsl.utils.registerLocalFix
import arrow.meta.ide.dsl.utils.removeElement
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.diagnostic.ProofRenderer
import arrow.meta.phases.analysis.diagnostic.onPublishedApi
import arrow.meta.plugins.proofs.phases.ArrowRefined
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.allGivenProofs
import arrow.meta.plugins.proofs.phases.asString
import arrow.meta.plugins.proofs.phases.extensionProofs
import arrow.meta.plugins.proofs.phases.refinementProofs
import arrow.meta.plugins.proofs.phases.resolve.disallowedUserDefinedAmbiguities
import arrow.meta.plugins.proofs.phases.resolve.incorrectRefinement
import arrow.meta.plugins.proofs.phases.resolve.isPublishedInternalOrphan
import arrow.meta.plugins.proofs.phases.resolve.isViolatingOwnershipRule
import arrow.meta.plugins.proofs.phases.resolve.tooManyRefinements
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.textRangeWithoutComments
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs


internal val IdeMetaPlugin.publishedInternalProofs: Annotator
  get() = Annotator { element, holder ->
    element.safeAs<KtDeclaration>()?.let { declaration ->
      declaration.bindingCtx()?.let { bindingContext ->
        declaration.project.getService(CompilerContext::class.java)?.let { ctx ->
          declaration.isPublishedInternalOrphan(bindingContext)
            ?.onPublishedApi(bindingContext)
            ?.safeAs<Pair<KtAnnotationEntry, TextRange>>()?.let {
              holder.registerProhibitedProof(it)
            }
        }
      }
    }
  }

internal val IdeMetaPlugin.ownershipViolations: Annotator
  get() = Annotator { element, holder ->
    element.safeAs<KtDeclaration>()?.let { declaration ->
      declaration.bindingCtx()?.let { bindingContext ->
        declaration.project.getService(CompilerContext::class.java)?.let { ctx ->
          declaration.isViolatingOwnershipRule(bindingContext, ctx)?.let {
            holder.registerOwnershipViolation(it)
          }
        }
      }
    }
  }

internal val IdeMetaPlugin.proofAmbiguities: Annotator
  get() = Annotator { element, holder ->
    element.safeAs<KtDeclaration>()?.let { declaration ->
      element.project.getService(CompilerContext::class.java)?.let { ctx: CompilerContext ->
        ctx.run { extensionProofs() + allGivenProofs() + refinementProofs() }
          .disallowedUserDefinedAmbiguities().firstOrNull { (it, conflicts) ->
            val (proof, _) = it
            declaration.resolveToDescriptorIfAny()?.fqNameSafe == proof.through.fqNameSafe
          }?.let { (ambiguity, conflict) ->
            val (proof, _) = ambiguity
            holder.registerAmbiguousProofs(proof, declaration.textRangeWithoutComments, conflict)
          }
      }
    }
  }

internal val IdeMetaPlugin.incorrectAndTooManyRefinements: Annotator
  get() = Annotator { element, holder ->
    element.safeAs<KtClass>()
      ?.takeIf { it.hasModifier(KtTokens.INLINE_KEYWORD) }
      ?.run {
        resolveToDescriptorIfAny()?.let { descriptor ->
          bindingCtx()?.let { bindingContext ->
            incorrectRefinement(descriptor, bindingContext) { expectedFrom, expectedTo ->
              holder.registerIncorrectRefinement(this, expectedFrom, expectedTo)
            }
            tooManyRefinements(descriptor, bindingContext) { expectedFrom, expectedTo ->
              holder.registerTooManyRefinements(this, expectedFrom, expectedTo)
            }
          }
        }
      }
  }


private fun AnnotationHolder.registerProhibitedProof(publishedApi: Pair<KtAnnotationEntry, TextRange>): Unit =
  newAnnotation(HighlightSeverity.ERROR, "Internal overrides of proofs are not permitted to be published, as they break coherent proof resolution over the kotlin ecosystem.")
    .needsUpdateOnTyping()
    .range(publishedApi.second)
    .registerLocalFix(
      removeElement("Remove the @PublishedApi annotation", publishedApi.first),
      publishedApi.first,
      highlightType = ProblemHighlightType.ERROR
    )
    .universal()
    .registerFix()
    .create()

private fun AnnotationHolder.registerOwnershipViolation(on: Pair<KtDeclaration, Proof>): Unit =
  on.let { (declaration, proof) ->
    newAnnotation(HighlightSeverity.ERROR, "This ${proof.asString()} violates ownership rules,\nbecause public Proofs over 3rd party Types break coherence over the kotlin ecosystem.\nOne way to solve this is to declare the Proof as an internal orphan.")
      .needsUpdateOnTyping()
      .range(declaration.textRangeWithoutComments)
      .registerLocalFix(
        localQuickFix("Turn Proof into internal orphan") {
          declaration.addModifier(KtTokens.INTERNAL_KEYWORD)
        },
        declaration,
        highlightType = ProblemHighlightType.ERROR
      )
      .universal()
      .registerFix()
      .create()
  }


private fun <A : Proof> AnnotationHolder.registerAmbiguousProofs(proof: A, range: TextRange, conflicts: List<A>): Unit =
  newAnnotation(HighlightSeverity.ERROR,
    """
      This ${proof.asString()}
      ${"\n"}has following conflicting proof/s:
      ${"\n"}${conflicts.joinToString(separator = ",\n") { it.asString() }}
      ${"\n"}Please disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.
        """".trimIndent()
  ).needsUpdateOnTyping()
    .range(range)
    .create()

// TODO: Add another LocalFix to remove the SuperType Refined from [on]
private fun AnnotationHolder.registerTooManyRefinements(on: KtObjectDeclaration, expectedFrom: KotlinType, expectedTo: KotlinType): Unit =
  newAnnotation(HighlightSeverity.ERROR, "Refinements can only be defined over one companion object, which implements ${ArrowRefined.asString()}<${ProofRenderer.renderType(expectedFrom)},${ProofRenderer.renderType(expectedTo)}>.\nPlease remove this object or remove the superType Refined.")
    .needsUpdateOnTyping()
    .range(on.textRangeWithoutComments)
    .registerLocalFix(
      removeElement("Remove this Refinement", on),
      on,
      highlightType = ProblemHighlightType.ERROR
    )
    .universal()
    .registerFix()
    .create()

// TODO: Add LocalFix to substitute the Type Arguments
private fun AnnotationHolder.registerIncorrectRefinement(on: KtObjectDeclaration, expectedFrom: KotlinType, expectedTo: KotlinType): Unit =
  newAnnotation(HighlightSeverity.ERROR, "This Refinement can only be defined over one companion object, which implements ${ArrowRefined.asString()}<${ProofRenderer.renderType(expectedFrom)},${ProofRenderer.renderType(expectedTo)}>")
    .needsUpdateOnTyping()
    .range(on.textRangeWithoutComments)
    .create()