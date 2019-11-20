package arrow.meta.quotes.expression

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.Quote
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * A [KtWhileExpression] [Quote] with a custom template destructuring [WhileExpression]
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
  map: WhileExpression.(KtWhileExpression) -> Transform<KtWhileExpression>
): ExtensionPhase =
  quote(match, map) { WhileExpression(it) }

/**
 * A template destructuring [Scope] for a [KtWhileExpression]
 */
class WhileExpression(
  override val value: KtWhileExpression,
  val `(condition)`: Scope<KtExpression> = Scope(value.condition)
) : LoopExpressionScope<KtWhileExpression>(value)