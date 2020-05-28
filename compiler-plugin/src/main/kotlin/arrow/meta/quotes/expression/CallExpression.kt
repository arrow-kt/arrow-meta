package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtTypeProjection

/**
 * <code>"""$calleeExpression$argumentList""".callExpression</code>
 *
 * A template destructuring [Scope] for a [KtCallExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.callExpression
 *
 * val Meta.reformatBlock: CliPlugin
 *   get() =
 *     "Reformat Call Expression" {
 *       meta(
 *         callExpression({ true }) { expression ->
 *           Transform.replace(
 *             replacing = expression,
 *             newDeclaration = """$calleeExpression$argumentList""".callExpression
 *           )
 *         }
 *       )
 *     }
 */
class CallExpression(
  override val value: KtCallExpression,
  val calleeExpression: Scope<KtExpression>? = Scope(value.calleeExpression),
  val argumentList: Scope<KtValueArgumentList>? = Scope(value.valueArgumentList),
  val typeArgumentList: Scope<KtTypeArgumentList>? = Scope(value.typeArgumentList),
  val lambdaArguments: ScopedList<KtLambdaArgument>? = ScopedList(value.lambdaArguments),
  val valueArguments: ScopedList<KtValueArgument>? = ScopedList(value.valueArguments),
  val typeArguments: ScopedList<KtTypeProjection>? = ScopedList(value.typeArguments)
): Scope<KtCallExpression>(value) {
  override fun ElementScope.identity(): Scope<KtCallExpression> =
    """$calleeExpression(${argumentList?.value?.arguments?.joinToString(", ") { it.text }})""".callExpression
}
