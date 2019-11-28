package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * <code>""" $condition """.whenCondition</code>
 *
 * A template destructuring [Scope] for a [KtWhenExpression].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.whenExpression
 *
 * val Meta.reformatWhen: Plugin
 *  get() =
 *    "ReformatWhen" {
 *      meta(
 *       whenExpression({ true }) { e ->
 *        Transform.replace(
 *         replacing = e,
 *         newDeclaration =
 *         """|when $`(expression)` {
 *            | $entries
 *            | $`else`
 *            |} """.`when`
 *        )})
 *     }
 * ```
 */
class WhenExpression(
  override val value: KtWhenExpression?,
  val entries: ScopedList<KtWhenEntry> = ScopedList(value?.entries.orEmpty()),
  val variable: Property = Property(value?.subjectVariable),
  val `(expression)`: Scope<KtExpression> = Scope(value?.subjectExpression),
  val `else`: Scope<KtExpression> = Scope(value?.elseExpression)
) : Scope<KtWhenExpression>(value)