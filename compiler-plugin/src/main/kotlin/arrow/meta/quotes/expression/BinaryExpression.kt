package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

/**
 * <code>"""$left $operationReference $right""".binaryExpression</code>
 *
 * A template destructuring [Scope] for a [KtBinaryExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.binaryExpression
 *
 * val Meta.reformatBinary: CliPlugin
 *    get() =
 *      "Reformat Binary Expression" {
 *        meta(
 *          binaryExpression(this, { true }) { expression ->
 *            Transform.replace(
 *              replacing = expression,
 *              newDeclaration = """$left $operationReference $right""".binaryExpression
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class BinaryExpression(
  override val value: KtBinaryExpression,
  val left: Scope<KtExpression>? = Scope(value.left),
  val right: Scope<KtExpression>? = Scope(value.right),
  val operationReference: Scope<KtOperationReferenceExpression>? = Scope(value.operationReference)
): Scope<KtBinaryExpression>(value) {

  override fun ElementScope.identity(): BinaryExpression =
    """$left $operationReference $right""".binaryExpression
}