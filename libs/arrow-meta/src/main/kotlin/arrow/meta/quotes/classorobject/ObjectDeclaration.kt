package arrow.meta.quotes.classorobject

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * <code>""" object $name { $body }""".objectDeclaration</code>
 *
 * A template destructuring [Scope] for a [KtObjectDeclaration].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.objectDeclaration
 *
 * val Meta.reformatObjectDeclaration: CliPlugin
 *    get() =
 *      "ReformatObjectDeclaration" {
 *        meta(
 *          objectDeclaration(this, { element.isObjectLiteral() }) {
 *            Transform.replace(
 *              replacing = it.element,
 *              newDeclaration = """
 *                  | $`@annotations` object $name $superTypes {
 *                  |   $body
 *                  | }
 *                  | """.`object`
 *              )
 *            }
 *          )
 *        }
 * ```
 */
class ObjectDeclaration(
  override val value: KtObjectDeclaration,
  override val descriptor: ClassDescriptor?
) : ClassOrObjectScope<KtObjectDeclaration>(value, descriptor) {

  override fun ElementScope.identity(): ObjectDeclaration =
    """
    | $`@annotations` object $name $superTypes {
    |   $body
    | }
    | """.`object`
}
