package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * A [KtWhenExpression] [Quote] with a custom template destructuring [WhenExpression].  See below:
 *
 * ```kotlin:ank:silent
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
 *
 * @param match designed to to feed in any kind of [KtWhenExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.whenExpression(
  match: KtWhenExpression.() -> Boolean,
  map: WhenExpression.(KtWhenExpression) -> Transform<KtWhenExpression>
): ExtensionPhase =
  quote(match, map) { WhenExpression(it) }

/**
 * A template destructuring [Scope] for a [KtWhenExpression]
 */
class WhenExpression(
  override val value: KtWhenExpression?,
  val entries: ScopedList<KtWhenEntry> = ScopedList(value?.entries ?: listOf()),
  val variable: Property = Property(value?.subjectVariable),
  val `(expression)`: Scope<KtExpression> = Scope(value?.subjectExpression),
  val `else`: Scope<KtExpression> = Scope(value?.elseExpression)
) : Scope<KtWhenExpression>(value)