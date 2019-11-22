package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier

/**
 * A [KtModifierList] [Quote] with a custom template destructuring [ModifierList].  See below:
 *
 *```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.modifierList
 *
 * val Meta.reformatModifier: Plugin
 *  get() =
 *  "ReformatModifier" {
 *   meta(
 *    modifierList({ true }) { l ->
 *     Transform.replace(
 *      replacing = l,
 *      newDeclaration = """ $`@annotations` $modifier value """.`modifierList`
 *     )
 *    }
 *   )
 *  }
 *```
 *
 * @param match designed to to feed in any kind of [KtModifierList] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.modifierList(
  match: KtModifierList.() -> Boolean,
  map: ModifierList.(KtModifierList) -> Transform<KtModifierList>
): ExtensionPhase =
  quote(match, map) { ModifierList(it) }

/**
 * A template destructuring [Scope] for a [KtModifierList]
 */
class ModifierList(
  override val value: KtModifierList?,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries ?: listOf()),
  val modifier: PsiElement? = value?.visibilityModifier()
) : Scope<KtModifierList>(value)