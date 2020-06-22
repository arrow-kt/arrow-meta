package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.bindingCtx
import arrow.meta.ide.dsl.utils.registerLocalFix
import arrow.meta.ide.dsl.utils.removeElement
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.extensionProofs
import arrow.meta.plugins.proofs.phases.hasAnnotation
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.resolve.disallowedAmbiguities
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.textRangeWithoutComments
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.RenderingFormat
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.proofAnnotator: ExtensionPhase
  get() = Composite(
    addAnnotator(
      annotator = Annotator { element, holder ->
        // prohibited published internal Proofs
        element.safeAs<KtDeclaration>()?.let { declaration ->
          declaration.bindingCtx()?.let { ctx ->
            declaration.isPublishedInternalOrphan(ctx)?.let { prohibitedProof ->
              prohibitedProof.onPublishedApi(ctx)?.let { publishedApi ->
                holder.registerProhibitedProof(publishedApi)
              }
            }
          }
        }
      }
    ),
    addAnnotator(
      annotator = Annotator { element, holder ->
        // ambeguity
        element.safeAs<KtNamedFunction>()?.let { ff: KtNamedFunction ->
          element.project.getService(CompilerContext::class.java)?.let { ctx: CompilerContext ->
            ctx.extensionProofs()
              .disallowedAmbiguities()
              .firstOrNull { (f, _) ->
                f.second == ff //|| f.first.through == ff.resolveToDescriptorIfAny()
              }
              ?.let { (f, conflicts) ->
                f.second.identifyingElement?.textRangeInParent?.let { holder.registerAmbiguousProofs(f.first, it, conflicts) }
              }
          }
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


private fun AnnotationHolder.registerAmbiguousProofs(proof: ExtensionProof, range: TextRange, conflicts: List<ExtensionProof>): Unit =
  newAnnotation(HighlightSeverity.ERROR,
    DescriptorRenderer.COMPACT_WITH_SHORT_TYPES.withOptions {
      textFormat = RenderingFormat.HTML
    }.run {
      """
      |The proof
      |${render(proof.through)}
      |is in Conflict with the following proofs:
      |${conflicts.joinToString(separator = "<br/>") { render(it.through) }}
      |Please take following measures to disambiguate proof resolution: TODO!
        """".trimIndent()
    }
  ).needsUpdateOnTyping()
    .range(range)
    .create()

internal fun KtDeclaration.isPublishedInternalOrphan(ctx: BindingContext): KtDeclaration? =
  takeIf {
    it.isProof(ctx) &&
      it.hasAnnotation(ctx, KotlinBuiltIns.FQ_NAMES.publishedApi) &&
      it.hasModifier(KtTokens.INTERNAL_KEYWORD)
  }

internal fun KtDeclaration.onPublishedApi(ctx: BindingContext): Pair<KtAnnotationEntry, TextRange>? =
  annotationEntries.firstOrNull { ctx.get(BindingContext.ANNOTATION, it)?.fqName == KotlinBuiltIns.FQ_NAMES.publishedApi }
    ?.let { it to it.textRangeWithoutComments }

