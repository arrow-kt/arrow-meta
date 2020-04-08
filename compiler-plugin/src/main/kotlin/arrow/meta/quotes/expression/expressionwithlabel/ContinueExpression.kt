package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * <code>"""continue""".`continue`</code>
 *
 * A template destructuring [Scope] for a [KtContinueExpression].
 *
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.continueExpression
 *
 * val Meta.reformatContinue: CliPlugin
 *    get() =
 *      "Reformat Continue Expression" {
 *        meta(
 *          continueExpression({ true }) { expressionWithLabel ->
 *            Transform.replace(
 *              replacing = expressionWithLabel,
 *              newDeclaration = when {
 *                targetLabel.value != null -> """continue$targetLabel""".`continue`
 *                else -> """continue""".`continue`
 *              }
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class ContinueExpression(
  override val value: KtContinueExpression,
  override val targetLabel: Scope<KtSimpleNameExpression> = Scope(value.getTargetLabel()),
  override val labelName: String? = value.getLabelName() ?: "continue"
) : ExpressionWithLabel<KtContinueExpression>(value) {

  override fun ElementScope.identity(): ContinueExpression =
    when {
      targetLabel.value != null -> """continue$targetLabel""".`continue`
      else -> """continue""".`continue`
    }
}