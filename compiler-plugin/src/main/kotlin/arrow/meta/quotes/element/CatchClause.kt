package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * <code>```""" catch $`(parameter)` $`{ body }` """.catchClause```</code>
 *
 * A template destructuring [Scope] for a [KtCatchClause]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.catchClause
 *
 * val Meta.reformatCatchClause: Plugin
 *  get() =
 *   "ReformatCatchClause" {
 *    meta(
 *     catchClause({ true }) { c ->
 *      Transform.replace(
 *       replacing = c,
 *       newDeclaration = """ catch $`(parameter)` $`{ body }` """.catch
 *      )
 *     }
 *    )
 *   }
 *```
 */
class CatchClause(
  override val value: KtCatchClause?,
  val `(parameter)`: Parameter = Parameter(value?.catchParameter),
  val `{ body }`: Scope<KtExpression> = Scope(value?.catchBody),
  val parameterList: Scope<KtParameterList> = Scope(value?.parameterList)
) : Scope<KtCatchClause>(value)