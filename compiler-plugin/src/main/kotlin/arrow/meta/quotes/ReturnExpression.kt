package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReturnExpression

/**
 * A [KtReturnExpression] [Quote] with a custom template destructuring [ReturnExpressionScope]. See below:
 *
 * ```kotlin:ank:silent
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
 *       newDeclaration = """ $`return` """.`return`
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 * @param match designed to to feed in any kind of [KtReturnExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.returnExpression(
  match: KtReturnExpression.() -> Boolean,
  map: ReturnExpressionScope.(KtReturnExpression) -> Transform<KtReturnExpression>
): ExtensionPhase =
  quote(match, map) { ReturnExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtReturnExpression]
 */
class ReturnExpressionScope(
  override val value: KtReturnExpression?,
  val `return`: Scope<KtExpression> = Scope(value?.returnedExpression)
) : Scope<KtReturnExpression>(value)