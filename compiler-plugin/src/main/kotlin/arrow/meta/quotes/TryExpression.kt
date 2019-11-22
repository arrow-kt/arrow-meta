package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtTryExpression

/**
 * A [KtTryExpression] [Quote] with a custom template destructuring [TryExpression]. See below:
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
 *
 * @param match designed to to feed in any kind of [KtTryExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.tryExpression(
  match: KtTryExpression.() -> Boolean,
  map: TryExpression.(KtTryExpression) -> Transform<KtTryExpression>
): ExtensionPhase =
  quote(match, map) { TryExpression(it) }

/**
 * A template destructuring [Scope] for a [KtTryExpression]
 */
class TryExpression(
  override val value: KtTryExpression?,
  val tryBlock: Scope<KtBlockExpression> = Scope(value?.tryBlock),
  val catch: ScopedList<KtCatchClause> = ScopedList(value?.catchClauses ?: listOf()),
  val finally: Scope<KtFinallySection> = Scope(value?.finallyBlock)
) : Scope<KtTryExpression>(value)