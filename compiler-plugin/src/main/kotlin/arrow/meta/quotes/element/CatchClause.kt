package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression

/**
 * <code>"""catch ($parameter) $`{ catchBody }`""".catch</code>
 *
 * A template destructuring [Scope] for a [KtCatchClause].
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
 *       newDeclaration = """catch ($parameter) $`{ catchBody }`""".catch
 *      )
 *     }
 *    )
 *   }
 *```
 */
class CatchClause(
  override val value: KtCatchClause?,
  val parameter: Parameter = Parameter(value?.catchParameter),
  val `{ catchBody }`: Scope<KtExpression> = Scope(value?.catchBody)
) : Scope<KtCatchClause>(value) {
  override fun ElementScope.identity(): CatchClause =
    """catch ($parameter) $`{ catchBody }`""".catch
}