package arrow.meta.quotes.modifierlist

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
 * ```kotlin:ank:silent
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
 *    modifierList({ true }){ l ->
 *     Transform.replace(
 *      replacing = l,
 *      newDeclaration = """ $`@annotations` $modifier value """.`modifierList`
 *     )
 *    }
 *   )
 *  }
 *```
 */
class ModifierList(
  override val value: KtModifierList?,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries
    ?: listOf()),
  val modifier: PsiElement? = value?.visibilityModifier()
) : Scope<KtModifierList>(value)