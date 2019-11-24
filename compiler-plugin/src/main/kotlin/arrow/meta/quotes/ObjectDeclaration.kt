package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.parentscopes.ClassOrObjectScope
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * A template destructuring [Scope] for a [KtObjectDeclaration]
 *
 * @param match designed to to feed in any kind of [KtObjectDeclaration] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.objectDeclaration(
  match: KtObjectDeclaration.() -> Boolean,
  map: ObjectDeclaration.(KtObjectDeclaration) -> Transform<KtObjectDeclaration>
): ExtensionPhase =
  quote(match, map) { ObjectDeclaration(it) }

/**
 * A template destructuring [Scope] for a [KtObjectDeclaration]
 */
class ObjectDeclaration(
  override val value: KtObjectDeclaration,
  val textOffset: Int = value.textOffset
): ClassOrObjectScope<KtObjectDeclaration>(value)