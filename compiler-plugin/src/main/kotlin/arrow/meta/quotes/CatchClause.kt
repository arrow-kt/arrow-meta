package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * A [KtCatchClause] [Quote] with a custom template destructuring [CatchClause]. See below:
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
 *
 * @param match designed to to feed in any kind of [KtCatchClause] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.catchClause(
  match: KtCatchClause.() -> Boolean,
  map: CatchClause.(KtCatchClause) -> Transform<KtCatchClause>
): ExtensionPhase =
  quote(match, map) { CatchClause(it) }

/**
 * A template destructuring [Scope] for a [KtCatchClause]
 */
class CatchClause(
        override val value: KtCatchClause?,
        val `(parameter)`: Parameter = Parameter(value?.catchParameter),
        val `{ body }`: Scope<KtExpression> = Scope(value?.catchBody),
        val parameterList: Scope<KtParameterList> = Scope(value?.parameterList)
) : Scope<KtCatchClause>(value)