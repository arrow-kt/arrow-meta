package arrow.meta.quotes.expression.loopexpression

import arrow.meta.quotes.Scope
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * <code>""" for $`(param)` { $body } """.`for`</code>
 *
 * A template destructuring [Scope] for a [KtForExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.forExpression
 *
 * val Meta.reformatFor: Plugin
 *   get() =
 *     "ReformatFor" {
 *       meta(
 *        forExpression({ true }) { e ->
 *          Transform.replace(
 *            replacing = e,
 *            newDeclaration = """ for $`(param)` { $body } """.`for`
 *          )
 *        }
 *       )
 *     }
 * ```
 */
class ForExpression(
  override val value: KtForExpression,
  val `(param)`: Parameter = Parameter(value.loopParameter),
  val range: Scope<KtExpression> = Scope(value.loopRange), // TODO KtExpression scope
  val destructuringDeclaration: DestructuringDeclaration = DestructuringDeclaration(value.destructuringDeclaration)
) : LoopExpression<KtForExpression>(value)