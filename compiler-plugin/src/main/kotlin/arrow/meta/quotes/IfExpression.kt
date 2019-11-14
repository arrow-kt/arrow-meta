package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * A [KtIfExpression] [Quote] with a custom template destructuring [IfExpressionScope].  See below:
 *
 *```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.ifExpression
 *
 * val Meta.reformatIf: Plugin
 *  get() =
 *  "ReformatIf" {
 *   meta(
 *    ifExpression({ true }) { e ->
 *     Transform.replace(
 *      replacing = e,
 *      newDeclaration = """ if $`(condition)` $then $`else` """.`if`
 *     )
 *    }
 *   )
 *  }
 *```
 *
 * * @param match designed to to feed in any kind of [KtIfExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.ifExpression(
  match: KtIfExpression.() -> Boolean,
  map: IfExpressionScope.(KtIfExpression) -> Transform<KtIfExpression>
): ExtensionPhase =
  quote(match, map) { IfExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtIfExpression]
 */
class IfExpressionScope(
  override val value: KtIfExpression?,
  val `else`: Scope<KtExpression> = Scope(value?.`else`),
  val `(condition)`: Scope<KtExpression> = Scope(value?.condition),
  val then: Scope<KtExpression> = Scope(value)
) : Scope<KtIfExpression>(value)