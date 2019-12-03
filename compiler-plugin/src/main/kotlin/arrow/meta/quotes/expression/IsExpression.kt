package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * <code>"""$left $operation $type""".`is`</code>
 *
 * A template destructuring [Scope] for a [KtIsExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.isExpression
 *
 * val Meta.reformatIs: Plugin
 *  get() =
 *  "ReformatIs" {
 *   meta(
 *    isExpression({ true }) { e ->
 *     Transform.replace(
 *      replacing = e,
 *      newDeclaration = """$left $operation $type""".`is`
 *     )
 *    }
 *   )
 *  }
 *```
 */
class IsExpression(
  override val value: KtIsExpression?,
  val left: Scope<KtExpression> = Scope(value?.leftHandSide),
  val operation: Scope<KtSimpleNameExpression> = Scope(value?.operationReference),
  val type: Scope<KtTypeReference> = Scope(value?.typeReference)
) : Scope<KtIsExpression>(value)