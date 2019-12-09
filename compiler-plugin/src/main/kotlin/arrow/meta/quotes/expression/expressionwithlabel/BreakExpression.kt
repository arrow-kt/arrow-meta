package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * <code>"""$labelName@$targetLabel""".`break`</code>
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
 * val Meta.reformatBreak: Plugin
 *  get() =
 *   "ReformatBreak" {
 *    meta(
 *     breakExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """$labelName@$targetLabel""".`break`
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class BreakExpression(
  override val value: KtBreakExpression,
  override val targetLabel: Scope<KtSimpleNameExpression> = Scope(value.getTargetLabel()),
  override val labelName: String? = value.getLabelName() ?: "break"
  ) : ExpressionWithLabel<KtBreakExpression>(value)