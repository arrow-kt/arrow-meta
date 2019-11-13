package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

/**
 * A template destructuring [Scope] for a [KtBinaryExpression]
 *
 * @param match designed to to feed in any kind of [KtBinaryExpression] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.binaryExpression(
  match: KtBinaryExpression.() -> Boolean,
  map: BinaryExpressionScope.(KtBinaryExpression) -> Transform<KtBinaryExpression>
): ExtensionPhase =
  quote(match, map) { BinaryExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtBinaryExpression]
 */
class BinaryExpressionScope(
  override val value: KtBinaryExpression,
  val left: Scope<KtExpression>? = Scope(value.left),
  val right: Scope<KtExpression>? = Scope(value.right),
  val operationReference: Scope<KtOperationReferenceExpression>? = Scope(value.operationReference)
): Scope<KtBinaryExpression>(value)