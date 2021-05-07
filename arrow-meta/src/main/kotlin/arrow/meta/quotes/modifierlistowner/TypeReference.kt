package arrow.meta.quotes.modifierlistowner

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * <code> """$`@annotations` $typeElement""".type </code>
 *
 * A template destructuring [Scope] for a [TypeReference].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.typeReference
 *
 * val Meta.changeTypeReference: CliPlugin
 *    get() =
 *      "ReformatModifier" {
 *        meta(
 *          typeReference(this, { true }) { modifierListOwner ->
 *            Transform.replace(
 *              replacing = modifierListOwner,
 *              newDeclaration = """$`@annotations` $typeElement""".type
 *            )
 *          }
 *        )
 *      }
 *```
 */
class TypeReference(
  override val value: KtTypeReference?,
  val typeElement: Scope<KtTypeElement>? = Scope(value?.typeElement), // TODO KtTypeElement scope and quote template
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(separator = " ", value = value?.annotationEntries.orEmpty())
): Scope<KtTypeReference>(value) {
  override fun ElementScope.identity(): TypeReference =
    """$`@annotations`$typeElement""".type
}