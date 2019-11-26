package arrow.meta.quotes.classorobject

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * A template destructuring [Scope] for a [KtObjectDeclaration]
 */
class ObjectDeclaration(
  override val value: KtObjectDeclaration
): ClassOrObjectScope<KtObjectDeclaration>(value)