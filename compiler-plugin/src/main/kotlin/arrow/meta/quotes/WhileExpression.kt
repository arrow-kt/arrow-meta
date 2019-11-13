package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * A [KtWhileExpression] [Quote] with a custom template destructuring [ParameterScope]
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
  val condition: Scope<KtExpression> = Scope(value.condition)
) : LoopExpressionScope<KtWhileExpression>(value)