package arrow.meta.quotes.expression

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.Quote
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtExpression

/**
 * A [KtAnnotatedExpression] [Quote] with a custom template destructuring [AnnotatedExpression]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.annotatedExpression
 *
 * val Meta.reformatAnnotated: Plugin
 *  get() =
 *   "ReformatAnnotated" {
 *    meta(
 *     annotatedExpression({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """ $`@annotations` $expression """.annotatedExpression
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 * @param match designed to to feed in any kind of [KtAnnotatedExpression] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.annotatedExpression(
  match: KtAnnotatedExpression.() -> Boolean,
  map: AnnotatedExpression.(KtAnnotatedExpression) -> Transform<KtAnnotatedExpression>
): ExtensionPhase =
  quote(match, map) { AnnotatedExpression(it) }

/**
 * A template destructuring [Scope] for a [KtAnnotatedExpression]
 */
class AnnotatedExpression(
  override val value: KtAnnotatedExpression?,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries
    ?: listOf()),
  val expression: Scope<KtExpression> = Scope(value?.baseExpression)
) : Scope<KtAnnotatedExpression>(value)