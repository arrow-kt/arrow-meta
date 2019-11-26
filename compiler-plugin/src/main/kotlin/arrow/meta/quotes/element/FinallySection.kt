package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFinallySection

/**
 * <code>""" $finally """.finally</code>
 *
 * A template destructuring [Scope] for a [KtFinallySection]. See below:
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
 */
class FinallySection(
  override val value: KtFinallySection?,
  val finally: Scope<KtBlockExpression> = Scope(value?.finalExpression)
) : Scope<KtFinallySection>(value)