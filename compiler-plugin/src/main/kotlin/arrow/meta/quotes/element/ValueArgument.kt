package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentName
import org.jetbrains.kotlin.psi.ValueArgument

/**
 * <code>"""argumentExpression""".argument</code>
 *
 * TODO add wrappers for certain boolean logic so that there are more options to create KtValueArgument
 * @see [DefaultElementScope::argument]
 *
 * A template destructuring [Scope] for a [ValueArgument].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.valueArgument
 *
 * val Meta.reformatValueArg: Plugin
 *  get() =
 *   "ReformatValueArg" {
 *    meta(
 *     valueArgument({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """argumentExpression""".argument
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
): Scope<KtValueArgument>(value)