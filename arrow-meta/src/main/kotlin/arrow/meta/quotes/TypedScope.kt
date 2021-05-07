package arrow.meta.quotes

import arrow.meta.phases.analysis.ElementScope
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtElement

open class TypedScope <out K : KtElement, D: DeclarationDescriptor>(
  override val value: K?,
  open val descriptor: D?
) : Scope<K>(value) {

  open fun ElementScope.identity(descriptor: D?): TypedScope<K, D> = TypedScope(value, descriptor)

  override fun ElementScope.identity(): Scope<K> = this@TypedScope
}