package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReturnExpression

/**
 * <code>"""return $`return`""".`return`</code>
 *
 * A template destructuring [Scope] for a [KtReturnExpression].
 *
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.returnExpression
 *
 * val Meta.reformatReturn: Plugin
 *  get() =
 *   "ReformatReturn" {
 *    meta(
 *     returnExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """$labelName $`return`""".`return`
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class ReturnExpression(
  override val value: KtReturnExpression,
  override val labelName: String? = value.getLabelName() ?: "return",
  val `return`: Scope<KtExpression> = Scope(value.returnedExpression)
) : ExpressionWithLabel<KtReturnExpression>(value)