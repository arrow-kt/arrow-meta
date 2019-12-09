package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * <code>"""break$targetLabel""".`break`</code>
 *
 * A template destructuring [Scope] for a [KtReturnExpression].
 *
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.breakExpression
 *
 * val Meta.reformatBreak: Plugin
 *  get() =
 *   "ReformatBreak" {
 *    meta(
 *     breakExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """continue$targetLabel""".`break`
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class ContinueExpression(
  override val value: KtContinueExpression,
  override val targetLabel: Scope<KtSimpleNameExpression> = Scope(value.getTargetLabel()),
  override val labelName: String? = value.getLabelName() ?: "continue"
) : ExpressionWithLabel<KtContinueExpression>(value)