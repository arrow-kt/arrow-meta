package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentName

/**
 * A template destructuring [Scope] for a [KtValueArgument]
 *
 * @param match designed to to feed in any kind of [KtValueArgument] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.valueArgument(
  match: KtValueArgument.() -> Boolean,
  map: ValueArgumentScope.(KtValueArgument) -> Transform<KtValueArgument>
): ExtensionPhase =
  quote(match, map) { ValueArgumentScope(it) }

/**
 * A template destructuring [Scope] for a [TypeReferenceScope]
 */
class ValueArgumentScope(
  override val value: KtValueArgument,
  val argumentExpression: Scope<KtExpression>? = Scope(value.getArgumentExpression()),
  val argumentName: Scope<KtValueArgumentName>? = Scope(value.getArgumentName()) // TODO KtValueArgumentName scope and quote template
): Scope<KtValueArgument>(value)