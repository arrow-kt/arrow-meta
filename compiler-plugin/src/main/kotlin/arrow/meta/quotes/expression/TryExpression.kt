package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtTryExpression

/**
 * <code>"""try $tryBlock$catchClauses$finallySection""".`try`</code>
 *
 * A template destructuring [Scope] for a [KtTryExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.tryExpression
 *
 * val Meta.reformatTry: Plugin
 *  get() =
 *   "ReformatTry" {
 *    meta(
 *     tryExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """try $tryBlock$catchClauses$finallySection""".`try`
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class TryExpression(
  override val value: KtTryExpression?,
  val tryBlock: Scope<KtBlockExpression> = Scope(value?.tryBlock),
  val catchClauses: ScopedList<KtCatchClause> = ScopedList(value = value?.catchClauses.orEmpty()),
  val finallySection: Scope<KtFinallySection> = Scope(value?.finallyBlock)
) : Scope<KtTryExpression>(value) {
  override fun ElementScope.identity(): Scope<KtTryExpression> =
    """try $tryBlock$catchClauses$finallySection""".`try`
}