package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * <code>"""$receiverExpression.$selectorExpression""".dotQualifiedExpression</code>
 *
 * A template destructuring [Scope] for a [KtDotQualifiedExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.dotQualifiedExpression
 *
 * val Meta.reformatDotQualifiedExpression: Plugin
 *  get() =
 *  "ReformatDotQualifiedExpression" {
 *   meta(
 *    dotQualifiedExpression({ true }) { e ->
 *     Transform.replace(
 *      replacing = e,
 *      newDeclaration = """$receiverExpression.$selectorExpression""".dotQualifiedExpression
 *     )
 *    }
 *   )
 *  }
 *```
 */
class DotQualifiedExpression(
  override val value: KtDotQualifiedExpression?,
  val receiverExpression: Scope<KtExpression> = Scope(value?.receiverExpression),
  val selectorExpression: Scope<KtExpression>? = Scope(value?.selectorExpression)
) : Scope<KtDotQualifiedExpression>(value) {
  override fun ElementScope.identity(): Scope<KtDotQualifiedExpression> =
    """$receiverExpression.$selectorExpression""".dotQualifiedExpression
}