package arrow.meta.quotes.element

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.Quote
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.KtWhenCondition

/**
 * A [KtWhenCondition] [Quote] with a custom template destructuring [WhenCondition]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whenCondition
 *
 * val Meta.reformatWhenCondition: Plugin
 *  get() =
 *   "ReformatWhenCondition" {
 *    meta(
 *     whenCondition({ true }) { c ->
 *      Transform.replace(
 *       replacing = c,
 *       newDeclaration = """ $condition """.whenCondition
 *      )
 *     }
 *    )
 *   }
 *```
 *
 * @param match designed to to feed in any kind of [KtWhenCondition] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.whenCondition(
  match: KtWhenCondition.() -> Boolean,
  map: WhenCondition.(KtWhenCondition) -> Transform<KtWhenCondition>
): ExtensionPhase =
  quote(match, map) { WhenCondition(it) }

/**
 * A template destructuring [Scope] for a [KtWhenCondition]
 */
class WhenCondition(
  override val value: KtWhenCondition?,
  val condition: String = value?.text ?: ""
) : Scope<KtWhenCondition>(value)