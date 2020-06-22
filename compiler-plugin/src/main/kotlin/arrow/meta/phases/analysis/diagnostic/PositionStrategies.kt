package arrow.meta.phases.analysis.diagnostic

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.diagnostics.PositioningStrategy
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclaration

@JvmField
val onPublishedInternalOrphan: PositioningStrategy<KtDeclaration> = object : PositioningStrategy<KtDeclaration>() {
  /**
   * Each [element] that is a witness of this strategy has an attribute that allows internal orphans to be published publicly.
   * e.g.: [PublishedApi] annotation
   */
  override fun mark(element: KtDeclaration): List<TextRange> =
    listOf(
      element.publishedApiAnnotation()?.textRange ?: element.textRange
    )
}

/**
 * matching on the shortName is valid, as the diagnostic is only applied, if the FqName correlates.
 */
fun KtDeclaration.publishedApiAnnotation(): KtAnnotationEntry? =
  annotationEntries.firstOrNull {
    it.shortName == KotlinBuiltIns.FQ_NAMES.publishedApi.shortName()
  }