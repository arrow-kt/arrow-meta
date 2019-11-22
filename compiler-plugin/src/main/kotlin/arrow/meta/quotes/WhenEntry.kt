package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry

/**
 * A [KtWhenEntry] [Quote] with a custom template destructuring [WhenEntry]. See below:
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
 *
 * @param match designed to to feed in any kind of [KtWhenEntry] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.whenEntry(
  match: KtWhenEntry.() -> Boolean,
  map: WhenEntry.(KtWhenEntry) -> Transform<KtWhenEntry>
): ExtensionPhase =
  quote(match, map) { WhenEntry(it) }

/**
 * A template destructuring [Scope] for a [KtWhenEntry]
 */
class WhenEntry(
  override val value: KtWhenEntry?,
  val conditions: ScopedList<KtWhenCondition> = ScopedList(value?.conditions?.toList() ?: listOf()),
  val expression: Scope<KtExpression> = Scope(value?.expression),
  val isElse: Boolean = value?.isElse == true
) : Scope<KtWhenEntry>(value)
