package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry

/**
 * <code>""" $conditions -> $expression """.whenEntry</code>
 *
 * A template destructuring [Scope] for a [KtWhenEntry].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whenEntry
 *
 * val Meta.reformatWhenEntry: Plugin
 *  get() =
 *   "ReformatWhenEntry" {
 *    meta(
 *     whenEntry({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """ $conditions -> $expression """.whenEntry
 *      )
 *     }
 *    )
 *   }
 * ```
 */
class WhenEntry(
  override val value: KtWhenEntry?,
  val conditions: ScopedList<KtWhenCondition> = ScopedList(value?.conditions?.toList()
    ?: listOf()),
  val expression: Scope<KtExpression> = Scope(value?.expression),
  val isElse: Boolean = value?.isElse == true
) : Scope<KtWhenEntry>(value)
