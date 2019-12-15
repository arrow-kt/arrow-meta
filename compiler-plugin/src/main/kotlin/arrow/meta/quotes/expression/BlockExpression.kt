package arrow.meta.quotes.expression

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * <code> """$statements""".block </code>
 *
 * A template destructuring [Scope] for a [KtBlockExpression].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.blockExpression
 *
 * val Meta.reformatBlock: Plugin
 *  get() =
 *   "BlockExpression" {
 *    meta(
 *     blockExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """$statements""".block
 *      )
 *     }
 *    )
 *   }
 */
class BlockExpression(
  override val value: KtBlockExpression?,
  val statements: ScopedList<KtExpression> = ScopedList(
    separator = "\n",
    value = value?.statements.orEmpty()
  )
) : Scope<KtBlockExpression>(value) {
  override fun ElementScope.identity(): BlockExpression =
    """$statements""".block
}