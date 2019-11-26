package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParameter

/**
 * <code>""" { $`(params)` -> $bodyExpression } """.lambdaExpression</code>
 *
 * A template destructuring [Scope] for a [KtLambdaExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.lambdaExpression
 *
 * val Meta.reformatFor: Plugin
 *   get() =
 *     "ReformatLambda" {
 *       meta(
 *        lambdaExpression({ true }) { e ->
 *          Transform.replace(
 *            replacing = e,
 *            newDeclaration = """ { $`(params)` -> $bodyExpression } """.lambdaExpression
 *          )
 *        }
 *       )
 *     }
 * ```
 */
class LambdaExpression(
  override val value: KtLambdaExpression,
  val functionLiteral: FunctionLiteral = FunctionLiteral(value.functionLiteral),
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters,
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val bodyExpression: BlockExpression = BlockExpression(value.bodyExpression),
  val containingKtFile: File = File(value.containingKtFile)
) : Scope<KtLambdaExpression>(value)