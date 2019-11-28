package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * <code>"""throw $thrownExpression""".`throw`</code>
 *
 * A template destructuring [Scope] for a [KtThrowExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.throwExpression
 *
 * val Meta.reformatThrow: Plugin
 *  get() =
 *   "ReformatThrow" {
 *    meta(
 *     throwExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """throw $thrownExpression""".`throw`
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class ThrowExpression(
  override val value: KtThrowExpression?,
  val thrownExpression: Scope<KtExpression> = Scope(value?.thrownExpression)
) : Scope<KtThrowExpression>(value)
