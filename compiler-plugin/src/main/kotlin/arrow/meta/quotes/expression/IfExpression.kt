package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * <code>"""if ($condition) $`else`""".`if`</code>
 *
 * A template destructuring [Scope] for a [KtIfExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.ifExpression
 *
 * val Meta.reformatIf: Plugin
 *  get() =
 *  "ReformatIf" {
 *   meta(
 *    ifExpression({ true }) { e ->
 *     Transform.replace(
 *      replacing = e,
 *      newDeclaration = """if ($condition) $`else`""".`if`
 *     )
 *    }
 *   )
 *  }
 *```
 */
class IfExpression(
  override val value: KtIfExpression?,
  val condition: Scope<KtExpression> = Scope(value?.condition),
  val then: Scope<KtExpression> = Scope(value),
  val `else`: Scope<KtExpression> = Scope(value?.`else`)
) : Scope<KtIfExpression>(value) {
  override fun ElementScope.identity(): Scope<KtIfExpression> =
    """if ($condition) $`else`""".`if`  // {"""$then""".`if`} also works
}