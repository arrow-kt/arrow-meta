package arrow.meta.quotes.expression.expressionwithlabel.instanceexpressionwithlabel

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtThisExpression

/**
 * <code>"""$instanceReference$targetLabel""".`this`</code>
 *
 * A template destructuring [Scope] for a [KtBreakExpression].
 *
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.thisExpression
 *
 * val Meta.reformatThisExpression: Plugin
 *  get() =
 *   "ReformatThis" {
 *     meta(
 *       thisExpression({ true }) { instanceExpressionWithLabel ->
 *         Transform.replace(
 *           replacing = instanceExpressionWithLabel,
 *           newDeclaration = when {
 *             targetLabel.value != null -> """$instanceReference$targetLabel""".`this`
 *             else -> """$instanceReference""".`this`
 *           }
 *         )
 *       }
 *     )
 *   }
 * ```
 */
class ThisExpression(
  override val value: KtThisExpression,
  override val labelName: String? = value.getLabelName()
) : InstanceExpressionWithLabel<KtThisExpression>(value) {
  override fun ElementScope.identity(): ThisExpression =
    when {
      targetLabel.value != null -> """$instanceReference$targetLabel""".`this`
      else -> """$instanceReference""".`this`
    }
}