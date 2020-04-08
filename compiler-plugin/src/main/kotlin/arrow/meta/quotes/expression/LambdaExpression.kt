package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParameter

/**
 * <code>lambdaExpression("""$`(params)`""", """$bodyExpression""")</code>
 *
 * A template destructuring [Scope] for a [KtLambdaExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.lambdaExpression
 *
 * val Meta.reformatFor: CliPlugin
 *   get() =
 *     "ReformatLambda" {
 *       meta(
 *        lambdaExpression({ true }) { e ->
 *          Transform.replace(
 *            replacing = e,
 *            newDeclaration = lambdaExpression("""$`(params)`""", """$bodyExpression""")
 *          )
 *        }
 *       )
 *     }
 * ```
 */
class LambdaExpression(
  override val value: KtLambdaExpression,
  val functionLiteral: FunctionLiteral = FunctionLiteral(value.functionLiteral),  // TODO locate an example or fix ValueArgument
  val `(params)`: ScopedList<KtParameter> = ScopedList(value = value.valueParameters),
  val bodyExpression: BlockExpression? = BlockExpression(value.bodyExpression)
) : Scope<KtLambdaExpression>(value) {
  override fun ElementScope.identity(): LambdaExpression =
    lambdaExpression("""$`(params)`""", """$bodyExpression""")
}