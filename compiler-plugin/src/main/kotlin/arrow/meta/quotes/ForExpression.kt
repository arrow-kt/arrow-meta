package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

/**
 * A [KtForExpression] [Quote] with a custom template destructuring [ForExpressionScope].  See below:
 *
 * ```kotlin:ank:silent
 * forExpression({ true }) { e ->
 *   Transform.replace(
 *    replacing = e,
 *    newDeclaration = """ for $`(param)` { $body } """.`for`
 *   )
 *  }
 * ```
 *
 * @param match designed to to feed in any kind of [KtForExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.forExpression(
  match: KtForExpression.() -> Boolean,
  map: ForExpressionScope.(KtForExpression) -> Transform<KtForExpression>
): ExtensionPhase =
  quote(match, map) { ForExpressionScope(it) }

/**
 * A template destructuring [Scope] for a [KtForExpression]
 */
class ForExpressionScope(
  override val value: KtForExpression,
  val `(param)`: ParameterScope = ParameterScope(value.loopParameter),
  val range: Scope<KtExpression> = Scope(value.loopRange), // TODO KtExpression scope
  val destructuringDeclaration: Scope<KtDestructuringDeclaration> = Scope(value.destructuringDeclaration) // TODO KtDestructuringDeclaration scope
) : LoopExpressionScope<KtForExpression>(value)