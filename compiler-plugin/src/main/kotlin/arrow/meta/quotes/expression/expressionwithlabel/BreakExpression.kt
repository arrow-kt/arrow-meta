package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * <code>"""break$targetLabel""".`break`</code>
 *
 * A template destructuring [Scope] for a [KtBreakExpression].
 *
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.breakExpression
 *
 * val Meta.reformatBreak: Plugin
 *    get() =
 *      "Reformat Break Expression" {
 *        meta(
 *          breakExpression({ true }) { expressionWithLabel ->
 *            Transform.replace(
 *              replacing = expressionWithLabel,
 *              newDeclaration = """break$targetLabel""".`break`
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class BreakExpression(
  override val value: KtBreakExpression,
  override val targetLabel: Scope<KtSimpleNameExpression> = Scope(value.getTargetLabel()),
  override val labelName: String? = value.getLabelName() ?: "break"
  ) : ExpressionWithLabel<KtBreakExpression>(value) {
  override fun ElementScope.identity(): BreakExpression =
    """break$targetLabel""".`break`
}