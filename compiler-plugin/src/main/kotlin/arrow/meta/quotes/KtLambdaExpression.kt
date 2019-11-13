package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParameter

/**
 * A [KtLambdaExpression] [Quote] with a custom template destructuring [LambdaExpressionScope]
 */
fun Meta.lambdaExpression(
  match: KtLambdaExpression.() -> Boolean,
  map: LambdaExpressionScope.(KtLambdaExpression) -> Transform<KtLambdaExpression>
) : ExtensionPhase =
  quote(match, map) { LambdaExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtLambdaExpression]
 */
class LambdaExpressionScope(
  override val value: KtLambdaExpression,
  val functionLiteral: Scope<KtFunctionLiteral> = Scope(value.functionLiteral), // TODO FunctionLiteral scope and quote template
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters,
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val bodyExpression: Scope<KtBlockExpression> = Scope(value.bodyExpression), // TODO Body Expression scope and quote template
  val containingLtFile: Scope<KtFile> = Scope(value.containingKtFile) // TODO KtFile scope and quote template
) : Scope<KtLambdaExpression>(value)