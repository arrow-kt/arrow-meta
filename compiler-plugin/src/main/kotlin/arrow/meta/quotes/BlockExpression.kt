package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * A [KtBlockExpression] [Quote] with a custom template destructuring [BlockExpressionScope]. See below:
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
  map: BlockExpressionScope.(KtBlockExpression) -> Transform<KtBlockExpression>
): ExtensionPhase =
  quote(match, map) { BlockExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtBlockExpression]
 */
class BlockExpressionScope(
  override val value: KtBlockExpression?,
  val statements: ScopedList<KtExpression> = ScopedList(value?.statements ?: listOf()),
  val firstStatement: Scope<KtExpression> = Scope(value?.firstStatement)
) : Scope<KtBlockExpression>(value)