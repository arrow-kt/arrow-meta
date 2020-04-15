package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentName

/**
 * <code>(if (!argumentName.toString().isNullOrEmpty()) """$argumentName = $argumentExpression""" else  """$argumentExpression""").argument</code>
 *
 * A template destructuring [Scope] for a [ValueArgument].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.valueArgument
 *
 * val Meta.reformatValueArg: CliPlugin
 *  get() =
 *   "ReformatValueArg" {
 *    meta(
 *     valueArgument({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = (if (!argumentName.toString().isNullOrEmpty()) """$argumentName = $argumentExpression""" else  """$argumentExpression""").argument
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 */
class ValueArgument(
  override val value: KtValueArgument,
  val argumentExpression: Scope<KtExpression>? = Scope(value.getArgumentExpression()), // TODO KtExpression scope and quote template
  val argumentName: Scope<KtValueArgumentName>? = Scope(value.getArgumentName()) // TODO KtValueArgumentName scope and quote template
): Scope<KtValueArgument>(value) {

  override fun ElementScope.identity(): ValueArgument =
    (if (!argumentName.toString().isNullOrEmpty()) """$argumentName = $argumentExpression""" else  """$argumentExpression""").argument
}