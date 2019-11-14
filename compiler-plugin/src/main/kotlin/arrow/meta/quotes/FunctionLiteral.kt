package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral

/**
 * A [KtFunctionLiteral] [Quote] with a custom template destructuring [FunctionLiteralScope]
 *
 * A function literal is a special notation to simplify how a function is defined.  There are two types of function literals in Kotlin:
 *  #### Lambda expression
 *  A lambda expression is a short way to define a function.  It tends to be more explicit than anonymous functions:
 * ```kotlin:ank:silent
 * val increment: (Int) -> Unit = { x -> x + 1 }
 * ```
 *  #### Anonymous function
 *  An anonynous function is just another way to define a function:
 * ```kotlin:ank:silent
 * val increment: (Int) -> Unit = fun(x) { x + 1 }
 * ```
 *
 * @param match designed to to feed in any kind of [KtFunctionLiteral] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.functionLiteral(
  match: KtFunctionLiteral.() -> Boolean,
  map: FunctionLiteralScope.(KtFunctionLiteral) -> Transform<KtFunctionLiteral>
) : ExtensionPhase =
  quote(match, map) { FunctionLiteralScope(it) }

/**
 * A template destructuring [Scope] for a [KtFunctionLiteral]
 */
class FunctionLiteralScope(
  override val value: KtFunctionLiteral,
  val name: Name? = value.nameAsName,
  val blockExpression: Scope<KtBlockExpression> = Scope(value.bodyBlockExpression) // TODO KtBlockExpression scope and quote template
) : Scope<KtFunctionLiteral>(value)