package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * <code>"""return""".`return`</code>
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
 *       newDeclaration = when {
 *          `return`.value != null -> """return $`return`""".`return`
 *          targetLabel.value != null -> """return$targetLabel""".`return`
 *          else -> """return""".`return`
 *         }
 *       )
 *     }
 *   )
 * }
 * ```
 */
class ReturnExpression(
  override val value: KtReturnExpression,
  override val targetLabel: Scope<KtSimpleNameExpression> = Scope(value.getTargetLabel()),
  val `return`: Scope<KtExpression> = Scope(value.returnedExpression)
) : ExpressionWithLabel<KtReturnExpression>(value) {
  override fun ElementScope.identity(): Scope<KtReturnExpression> =
    when {
      `return`.value != null -> """return $`return`""".`return`
      targetLabel.value != null -> """return$targetLabel""".`return`
      else -> """return""".`return`
    }
}