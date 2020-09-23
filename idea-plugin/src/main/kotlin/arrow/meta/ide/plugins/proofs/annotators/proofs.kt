package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.bindingCtx
import arrow.meta.ide.dsl.utils.registerLocalFix
import arrow.meta.ide.dsl.utils.removeElement
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.diagnostic.onPublishedApi
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.allGivenProofs
import arrow.meta.plugins.proofs.phases.asString
import arrow.meta.plugins.proofs.phases.extensionProofs
import arrow.meta.plugins.proofs.phases.refinementProofs
import arrow.meta.plugins.proofs.phases.resolve.disallowedUserDefinedAmbiguities
import arrow.meta.plugins.proofs.phases.resolve.isPublishedInternalOrphan
import arrow.meta.plugins.proofs.phases.resolve.isViolatingOwnershipRule
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.proofAnnotator: ExtensionPhase
  get() = Composite(
    addAnnotator(
      annotator = Annotator { element, holder ->

        element.safeAs<KtDeclaration>()?.let { declaration ->
          declaration.bindingCtx()?.let { bindingContext ->
            declaration.project.getService(CompilerContext::class.java)?.let { ctx ->
              declaration.isPublishedInternalOrphan(bindingContext)
                ?.onPublishedApi(bindingContext)
                ?.safeAs<Pair<KtAnnotationEntry, TextRange>>()?.let {
                  holder.registerProhibitedProof(it)
                }

              declaration.isViolatingOwnershipRule(bindingContext, ctx)?.let {
                holder.registerOwnershipViolation(it)
              }
            }
          }
        }
      }
    ),
    addAnnotator(
      annotator = Annotator { element, holder ->
        // proof resolution ambiguities
        element.project.getService(CompilerContext::class.java)?.let { ctx: CompilerContext ->
          val map = ctx.run { extensionProofs() + allGivenProofs() + refinementProofs() }
            .disallowedUserDefinedAmbiguities().toMap()

          /*element.safeAs<KtDeclaration>()
            ?.let {
              it.resolveToDescriptorIfAny()?.let { f ->
                map.firstOrNull { (ff, _) ->
                  ff.first.through.fqNameSafe == f.fqNameSafe
                }
              }?.run { it to this }
            }?.let { (f, ambiguities) ->
              val (proof, conflicts) = ambiguities
              holder.registerAmbiguousProofs(proof.first, f.textRangeWithoutComments, conflicts)
            }*/
        }
      }
    )
  )

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

private fun AnnotationHolder.registerOwnershipViolation(publishedApi: Pair<KtDeclaration, Proof>): Unit =
  TODO()


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
