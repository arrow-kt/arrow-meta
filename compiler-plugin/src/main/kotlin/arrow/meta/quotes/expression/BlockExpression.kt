package arrow.meta.quotes.expression

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.Quote
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * A [KtBlockExpression] [Quote] with a custom template destructuring [BlockExpression]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.blockExpression
 *
 * val Meta.reformatBlock: Plugin
 *  get() =
 *   "BlockExpression" {
 *    meta(
 *     blockExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """ { $statements } """.block
 *      )
 *     }
 *    )
 *   }
 *```
 *
 * @param match designed to to feed in any kind of [KtBlockExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.blockExpression(
  match: KtBlockExpression.() -> Boolean,
  map: BlockExpression.(KtBlockExpression) -> Transform<KtBlockExpression>
): ExtensionPhase =
  quote(match, map) { BlockExpression(it) }

/**
 * A template destructuring [Scope] for a [KtBlockExpression]
 */
class BlockExpression(
  override val value: KtBlockExpression?,
  val statements: ScopedList<KtExpression> = ScopedList(value?.statements
    ?: listOf()),
  val firstStatement: Scope<KtExpression> = Scope(value?.firstStatement)
) : Scope<KtBlockExpression>(value)