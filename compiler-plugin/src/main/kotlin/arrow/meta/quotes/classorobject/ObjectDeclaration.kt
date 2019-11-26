package arrow.meta.quotes.classorobject

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * <code>""" object $name { $body }""".objectDeclaration</code>
 *
 * @see [ElementScope::companionObject]
 *
 * A template destructuring [Scope] for a [KtObjectDeclaration].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.objectDeclaration
 *
 * val Meta.reformatObjectDeclaration: Plugin
 *   get() =
 *     "ReformatObjectDeclaration" {
 *       meta(
 *        objectDeclaration({ true }) { c ->
 *          Transform.replace(
 *            replacing = c,
 *            newDeclaration = """ object $name { $body }""".`object`
 *          )
 *        }
 *       )
 *     }
 * ```
 */
class ObjectDeclaration(
  override val value: KtObjectDeclaration
): ClassOrObjectScope<KtObjectDeclaration>(value)
