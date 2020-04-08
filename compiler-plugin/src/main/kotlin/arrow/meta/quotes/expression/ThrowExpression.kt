package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
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
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.throwExpression
 *
 * val Meta.reformatThrow: CliPlugin
 *    get() =
 *      "Reformat Throw Expression" {
 *        meta(
 *          throwExpression({ true }) { expression ->
 *            Transform.replace(
 *              replacing = expression,
 *              newDeclaration = """throw $thrownExpression""".`throw`
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class ThrowExpression(
  override val value: KtThrowExpression?,
  val thrownExpression: Scope<KtExpression> = Scope(value?.thrownExpression)
) : Scope<KtThrowExpression>(value) {
  override fun ElementScope.identity(): ThrowExpression =
    """throw $thrownExpression""".`throw`
}
