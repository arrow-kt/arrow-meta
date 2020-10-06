package arrow.meta.phases.analysis.diagnostic

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.ParametrizedDiagnostic
import org.jetbrains.kotlin.diagnostics.PositioningStrategy
import org.jetbrains.kotlin.diagnostics.hasSyntaxErrors
import org.jetbrains.kotlin.diagnostics.markElement
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.textRangeWithoutComments
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Each [element] that is a witness of this strategy has an attribute that allows internal orphans to be published publicly.
 * e.g.: [PublishedApi] annotation
 */
@JvmField
val onPublishedInternalOrphan: PositioningStrategy<KtDeclaration> =
  position(
    mark = {
      listOf(
        it.publishedApiAnnotation()?.textRange ?: it.textRange
      )
    }
  )

@JvmField
val onNavigationElement: PositioningStrategy<KtDeclaration> =
  position(
    mark = {
      listOf(
        it.navigationElement?.textRange ?: it.textRange
      )
    }
  )

@JvmField
val onIdentifyingElement: PositioningStrategy<KtObjectDeclaration> =
  position(
    mark = {
      listOf(
        it.identifyingElement?.textRange ?: it.textRange
      )
    }
  )

/**
 * matching on the shortName is valid, as the diagnostic is only applied, if the FqName correlates.
 * Which is checked prior to the Diagnostic being applied.
 */
fun KtDeclaration.publishedApiAnnotation(): KtAnnotationEntry? =
  annotationEntries.firstOrNull {
    it.shortName == KotlinBuiltIns.FQ_NAMES.publishedApi.shortName()
  }

fun KtDeclaration.onPublishedApi(ctx: BindingContext): Pair<KtAnnotationEntry, TextRange>? =
  annotationEntries.firstOrNull { ctx.get(BindingContext.ANNOTATION, it)?.fqName == KotlinBuiltIns.FQ_NAMES.publishedApi }
    ?.let { it to it.textRangeWithoutComments }

fun <A : PsiElement> position(
  mark: (A) -> List<TextRange> = { markElement(it) },
  isValid: (A) -> Boolean = { !hasSyntaxErrors(it) },
  markDiagnostic: (ParametrizedDiagnostic<out A>) -> List<TextRange> = { mark(it.psiElement) }
): PositioningStrategy<A> =
  object : PositioningStrategy<A>() {
    override fun mark(element: A): List<TextRange> =
      mark(element)

    override fun isValid(element: A): Boolean =
      isValid(element)

    override fun markDiagnostic(diagnostic: ParametrizedDiagnostic<out A>): List<TextRange> =
      markDiagnostic(diagnostic)
  }
