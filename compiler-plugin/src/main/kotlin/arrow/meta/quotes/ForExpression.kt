package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
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
  val `(param)`: ParameterScope = ParameterScope(value.loopParameter),
  val range: Scope<KtExpression> = Scope(value.loopRange),
  val destructuringDeclaration: Scope<KtDestructuringDeclaration> = Scope(value.destructuringDeclaration)
) : LoopExpressionScope<KtForExpression>(value)