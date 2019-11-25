package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * A [KtThrowExpression] [Quote] with a custom template destructuring [ThrowExpression]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.throwExpression
 *
 * val Meta.reformatThrow: Plugin
 *  get() =
 *   "ReformatThrow" {
 *    meta(
 *     throwExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """ $`throw` """.`throw`
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 * @param match designed to to feed in any kind of [KtThrowExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.throwExpression(
  match: KtThrowExpression.() -> Boolean,
  map: ThrowExpression.(KtThrowExpression) -> Transform<KtThrowExpression>
): ExtensionPhase =
  quote(match, map) { ThrowExpression(it) }

/**
 * A template destructuring [Scope] for a [KtThrowExpression]
 */
class ThrowExpression(
  override val value: KtThrowExpression?,
  val `throw`: Scope<KtExpression> = Scope(value?.thrownExpression)
) : Scope<KtThrowExpression>(value)