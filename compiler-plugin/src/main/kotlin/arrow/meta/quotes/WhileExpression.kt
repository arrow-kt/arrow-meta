package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.parentscopes.LoopExpressionScope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * A [KtWhileExpression] [Quote] with a custom template destructuring [WhileExpressionScope]
 *
 * @param match designed to to feed in any kind of [KtWhileExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 *
 * ```kotlin:ank:silent
 * whileExpression({ true }) { c ->
 *   Transform.replace(
 *     replacing = c,
 *     newDeclaration = """ while $`(condition)` { $body } """.`while`
 *   )
 * }
 *```
 */
fun Meta.whileExpression(
  match: KtWhileExpression.() -> Boolean,
  map: WhileExpressionScope.(KtWhileExpression) -> Transform<KtWhileExpression>
): ExtensionPhase =
  quote(match, map) { WhileExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtWhileExpression]
 */
class WhileExpressionScope(
  override val value: KtWhileExpression,
  val `(condition)`: Scope<KtExpression> = Scope(value.condition)
) : LoopExpressionScope<KtWhileExpression>(value)