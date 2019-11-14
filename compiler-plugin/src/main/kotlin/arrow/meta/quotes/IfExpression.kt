package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

private typealias ElseScope = Scope<KtExpression>
private typealias ConditionScope = Scope<KtExpression>
private typealias ThenScope = Scope<KtExpression>

/**
 * A [KtIfExpression] [Quote] with a custom template destructuring [IfExpressionScope].  See below:
 *
 * * @param match designed to to feed in any kind of [KtIfExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.ifExpression(
  match: KtIfExpression.() -> Boolean,
  map: IfExpressionScope.(KtIfExpression) -> Transform<KtIfExpression>
): ExtensionPhase =
  quote(match, map) { IfExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtIfExpression]
 */
class IfExpressionScope(
  override val value: KtIfExpression?,
  val `else`: ElseScope = Scope(value?.`else`),
  val `(condition)`: ConditionScope = Scope(value?.condition),
  val then: ThenScope = Scope(value)
) : Scope<KtIfExpression>(value)