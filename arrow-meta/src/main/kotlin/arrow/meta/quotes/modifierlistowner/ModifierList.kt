package arrow.meta.quotes.modifierlistowner

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier

/**
 * <code>""" $`@annotations` $modifier value """.`modifierList`</code>
 *
 * A template destructuring [Scope] for a [KtModifierList].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.modifierList
 *
 * val Meta.reformatModifierList: CliPlugin
 *    get() =
 *      "Reformat ModifierList" {
 *        meta(
 *          modifierList({ true }){ modifierList ->
 *            Transform.replace(
 *              replacing = modifierList,
 *              newDeclaration = """ $`@annotations` $modifier value """.`modifierList`
 *            )
 *          }
 *        )
 *      }
 *```
 */
class ModifierList(
  override val value: KtModifierList?,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries.orEmpty()),
  val modifier: PsiElement? = value?.visibilityModifier()
) : Scope<KtModifierList>(value)