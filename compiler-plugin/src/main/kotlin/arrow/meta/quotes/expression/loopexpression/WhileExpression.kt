package arrow.meta.quotes.expression.loopexpression

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * <code>""" while $`(condition)` { $body } """.`while`</code>
 *
 * A template destructuring [Scope] for a [KtWhileExpression]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whileExpression
 *
 * val Meta.reformatWhile: Plugin
 *  get() =
 *    "ReformatWhile" {
 *      meta(
 *       whileExpression({ true }) { e ->
 *        Transform.replace(
 *         replacing = e,
 *         newDeclaration = """ while $`(condition)` { $body } """.`while`
 *        )
 *       }
 *      )
 *     }
 * ```
 */
class WhileExpression(
  override val value: KtWhileExpression,
  val `(condition)`: Scope<KtExpression> = Scope(value.condition)
) : LoopExpression<KtWhileExpression>(value)