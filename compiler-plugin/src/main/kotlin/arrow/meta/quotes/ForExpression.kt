package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * A [KtForExpression] [Quote] with a custom template destructuring [ForExpressionScope]
 */
fun Meta.forExpression(
  match: KtForExpression.() -> Boolean,
  map: ForExpressionScope.(KtForExpression) -> Transform<KtForExpression>
): ExtensionPhase =
  quote(match, map) { ForExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtForExpression]
 */
class ForExpressionScope(
  override val value: KtForExpression,
  val `(forParameter)`: ParameterScope? = value.loopParameter?.let(::ParameterScope),
  val range: KtExpression? = value.loopRange
) : LoopExpressionScope<KtForExpression>(value)