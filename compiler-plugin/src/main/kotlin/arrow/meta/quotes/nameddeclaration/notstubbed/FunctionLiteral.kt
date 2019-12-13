package arrow.meta.quotes.nameddeclaration.notstubbed

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.expression.BlockExpression
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFunctionLiteral

// TODO: [KtTypeParameterListOwnerNotStubbed] is deprecated - rename package to JetTypeParameterListOwner when fully deprecated

/**
 * <code>"""{$`(params)`$blockExpression}""".functionLiteral</code>
 *
 * A template destructuring [Scope] for a [KtFunctionLiteral].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.functionLiteral
 *
 * val Meta.reformatFunctionLiteral: Plugin
 *  get() =
 *   "ReformatFunctionLiteral" {
 *    meta(
 *     functionLiteral({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """{$`(params)`$blockExpression}""".functionLiteral
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 *  A function literal is a special notation to simplify how a function is defined.  There are two types of function literals in Kotlin:
 *  #### Lambda expression
 *  A lambda expression is a short way to define a function.  It tends to be more explicit than anonymous functions:
 * ```kotlin:ank:silent
 * val increment: (Int) -> Unit = { x -> x + 1 }
 * ```
 *  #### Anonymous function
 *  An anonymous function is just another way to define a function:
 * ```kotlin:ank:silent
 * val increment: (Int) -> Unit = fun(x) { x + 1 }
 * ```
 */
class FunctionLiteral(
  override val value: KtFunctionLiteral,
  val name: Name? = value.nameAsName,
  val blockExpression: BlockExpression = BlockExpression(value.bodyBlockExpression)
) : FunctionNotStubbed<KtFunctionLiteral>(value) {
  override fun ElementScope.identity(): Scope<KtFunctionLiteral> =
    """{$`(params)`$blockExpression}""".functionLiteral
}