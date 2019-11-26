package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * <code>``` """ if $`(condition)` $then $`else` """.`if` ```</code>
 *
 * A template destructuring [Scope] for a [KtIfExpression]. See below:
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
 *      newDeclaration = """ if $`(condition)` $then $`else` """.`if`
 *     )
 *    }
 *   )
 *  }
 *```
 */
class IfExpression(
  override val value: KtIfExpression?,
  val `else`: Scope<KtExpression> = Scope(value?.`else`),
  val `(condition)`: Scope<KtExpression> = Scope(value?.condition),
  val then: Scope<KtExpression> = Scope(value)
) : Scope<KtIfExpression>(value)