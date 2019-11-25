package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFinallySection

/**
 * A [KtFinallySection] [Quote] with a custom template destructuring [FinallySection]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.finallySection
 *
 * val Meta.reformatFinallySection: Plugin
 *  get() =
 *   "ReformatFinallySection" {
 *    meta(
 *     finallySection({ true }) { s ->
 *      Transform.replace(
 *       replacing = s,
 *       newDeclaration = """ $finally """.finally
 *      )
 *     }
 *    )
 *   }
 *```
 *
 * @param match designed to to feed in any kind of [KtFinallySection] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.finallySection(
  match: KtFinallySection.() -> Boolean,
  map: FinallySection.(KtFinallySection) -> Transform<KtFinallySection>
): ExtensionPhase =
  quote(match, map) { FinallySection(it) }

/**
 * A template destructuring [Scope] for a [KtFinallySection]
 */
class FinallySection(
  override val value: KtFinallySection?,
  val finally: Scope<KtBlockExpression> = Scope(value?.finalExpression)
) : Scope<KtFinallySection>(value)