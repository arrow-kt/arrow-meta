package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtTryExpression

/**
 * <code>""" try $tryBlock $catch $finally """.tryExpression</code>
 *
 * A template destructuring [Scope] for a [KtTryExpression]. See below:
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
 *       newDeclaration = """ try $tryBlock $catch $finally """.`try`
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class TryExpression(
  override val value: KtTryExpression?,
  val tryBlock: Scope<KtBlockExpression> = Scope(value?.tryBlock),
  val catch: ScopedList<KtCatchClause> = ScopedList(value?.catchClauses
    ?: listOf()),
  val finally: Scope<KtFinallySection> = Scope(value?.finallyBlock)
) : Scope<KtTryExpression>(value)