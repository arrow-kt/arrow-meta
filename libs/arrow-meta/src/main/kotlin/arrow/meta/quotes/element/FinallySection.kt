package arrow.meta.quotes.element

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.expression.BlockExpression
import org.jetbrains.kotlin.psi.KtFinallySection

/**
 * <code>"""finally { $finallyExpression }""".finally</code>
 *
 * A template destructuring [Scope] for a [KtFinallySection].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.finallySection
 *
 * val Meta.reformatFinallySection: CliPlugin
 *  get() =
 *   "ReformatFinallySection" {
 *    meta(
 *     finallySection(this, { true }) { s ->
 *      Transform.replace(
 *       replacing = s,
 *       newDeclaration = """finally { $finallyExpression }""".finally
 *      )
 *     }
 *    )
 *   }
 *```
 */
class FinallySection(
  override val value: KtFinallySection?,
  val `{ finallyExpression }`: BlockExpression = BlockExpression(value?.finalExpression)
) : Scope<KtFinallySection>(value) {

  override fun ElementScope.identity(): FinallySection =
    """finally $`{ finallyExpression }`""".finally
}
