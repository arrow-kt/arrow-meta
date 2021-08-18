package arrow.meta.quotes.expression

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtExpression

/**
 * <code>""" $`@annotations` $expression """.annotatedExpression</code>
 *
 * A template destructuring [Scope] for a [KtAnnotatedExpression].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.annotatedExpression
 *
 * val Meta.reformatAnnotated: CliPlugin
 *    get() =
 *      "Reformat Annotated Expression" {
 *        meta(
 *          annotatedExpression({ true }) { expression ->
 *            Transform.replace(
 *              replacing = expression,
 *              newDeclaration = """ $`@annotations` $expression """.annotatedExpression
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class AnnotatedExpression(
  override val value: KtAnnotatedExpression?,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries.orEmpty()),
  val expression: Scope<KtExpression> = Scope(value?.baseExpression)
) : Scope<KtAnnotatedExpression>(value)
