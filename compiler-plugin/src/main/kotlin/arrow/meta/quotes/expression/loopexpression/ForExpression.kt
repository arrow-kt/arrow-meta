package arrow.meta.quotes.expression.loopexpression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * <code>"""for ($`(param)` in $loopRange) $body""".`for`</code>
 *
 * A template destructuring [Scope] for a [KtForExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.forExpression
 *
 * val Meta.reformatFor: CliPlugin
 *    get() =
 *    "Reformat For Expression" {
 *      meta(
 *        forExpression({ true }) { loopExpression ->
 *          Transform.replace(
 *            replacing = loopExpression,
 *            newDeclaration = if (destructuringDeclaration.entries.isEmpty()) {
 *                                """for ($`(param)` in $loopRange) $body""".`for`
 *                             } else {
 *                                """for ((${destructuringDeclaration.entries}) in $loopRange) $body""".`for`
 *                             }
 *          )
 *        }
 *      )
 *    }
 * ```
 */
class ForExpression(
  override val value: KtForExpression,
  val `(param)`: Parameter = Parameter(value.loopParameter),
  val loopRange: Scope<KtExpression> = Scope(value.loopRange),
  val destructuringDeclaration: DestructuringDeclaration = DestructuringDeclaration(value.destructuringDeclaration)
) : LoopExpression<KtForExpression>(value) {

  override fun ElementScope.identity(): ForExpression =
    if (destructuringDeclaration.entries.isEmpty()) {
      """for ($`(param)` in $loopRange) $body""".`for`
    } else {
      """for ((${destructuringDeclaration.entries}) in $loopRange) $body""".`for`
    }
}