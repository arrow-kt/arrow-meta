package arrow.meta.phases.analysis.diagnostic

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.diagnostics.PositioningStrategy
import org.jetbrains.kotlin.psi.KtDeclaration

@JvmField
val onPublishedInternalOrphan: PositioningStrategy<KtDeclaration> = object : PositioningStrategy<KtDeclaration>() {
  /**
   * Each [element] that is a witness of this strategy has an attribute that allows internal orphans to be published publicly.
   * e.g.: [PublishedApi] annotation
   */
  override fun mark(element: KtDeclaration): List<TextRange> =
    listOf(
      element
        .annotationEntries
        .firstOrNull {
          it.typeReference?.text == KotlinBuiltIns.FQ_NAMES.publishedApi.shortName().asString()
        }
        ?.textRange
        ?: element.textRange
    )
}

