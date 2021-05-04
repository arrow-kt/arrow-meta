package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.WhenScope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * <code> """when $`(expression)` { $entries } """.`when` </code>
 *
 * A template destructuring [Scope] for a [KtWhenExpression].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whenExpression
 *
 * val Meta.reformatWhen: CliPlugin
 *    get() =
 *      "Reformat When Expression" {
 *        meta(
 *          whenExpression({ true }) { expression ->
 *            Transform.replace(
 *              replacing = expression,
 *              newDeclaration =
 *                """|when $`(expression)` {
 *                   | $entries
 *                   |} """.`when`
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class WhenExpression(
  override val value: KtWhenExpression?,
  val entries: ScopedList<KtWhenEntry> = ScopedList(value = value?.entries.orEmpty(), separator = "\n"),
  val `(expression)`: Scope<KtExpression> = WhenScope(value?.subjectExpression),
  val `else`: Scope<KtExpression> = Scope(value?.elseExpression)
) : Scope<KtWhenExpression>(value) {
  override fun ElementScope.identity(): Scope<KtWhenExpression> =
    """|when $`(expression)` {
           | $entries
           |} """.`when`
}