package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry

/**
 * <code>(if (!isElse) """$conditions -> $expression""" else  """else -> $expression""").whenEntry</code>
 *
 * A template destructuring [Scope] for a [KtWhenEntry].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whenEntry
 *
 * val Meta.reformatWhenEntry: CliPlugin
 *  get() =
 *   "ReformatWhenEntry" {
 *    meta(
 *     whenEntry({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = (if (!isElse) """$conditions -> $expression""" else  """else -> $expression""").whenEntry
 *      )
 *     }
 *    )
 *   }
 * ```
 */

class WhenEntry(
  override val value: KtWhenEntry?,
  val conditions: ScopedList<KtWhenCondition> = ScopedList(
    separator = " && ",
    value = value?.conditions?.toList().orEmpty()
  ),
  val expression: Scope<KtExpression> = Scope(value?.expression),
  val isElse: Boolean = value?.isElse == true
) : Scope<KtWhenEntry>(value) {

  override fun ElementScope.identity(): WhenEntry =
    (if (!isElse) """$conditions -> $expression""" else  """else -> $expression""").whenEntry
}
