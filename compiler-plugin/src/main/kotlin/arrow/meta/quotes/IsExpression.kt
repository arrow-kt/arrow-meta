package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * A [KtIsExpression] [Quote] with a custom template destructuring [IsExpression]. See below:
 *
 *```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.isExpression
 *
 * val Meta.reformatIs: Plugin
 *  get() =
 *  "ReformatIs" {
 *   meta(
 *    isExpression({ true }) { e ->
 *     Transform.replace(
 *      replacing = e,
 *      newDeclaration = """ $left $operation $type """.`is`
 *     )
 *    }
 *   )
 *  }
 *```
 *
 * * @param match designed to to feed in any kind of [KtIsExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.isExpression(
  match: KtIsExpression.() -> Boolean,
  map: IsExpression.(KtIsExpression) -> Transform<KtIsExpression>
): ExtensionPhase =
  quote(match, map) { IsExpression(it) }

/**
 * A template destructuring [Scope] for a [KtIsExpression]
 */
class IsExpression(
  override val value: KtIsExpression?,
  val left: Scope<KtExpression> = Scope(value?.leftHandSide),
  val operation: Scope<KtSimpleNameExpression> = Scope(value?.operationReference),
  val type: Scope<KtTypeReference> = Scope(value?.typeReference)
) : Scope<KtIsExpression>(value)