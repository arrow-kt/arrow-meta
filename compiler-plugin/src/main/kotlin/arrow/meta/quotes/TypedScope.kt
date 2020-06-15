package arrow.meta.quotes

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtElement

open class TypedScope <out K : KtElement, out D: DeclarationDescriptor>(
  override val value: K?,
  open val typeInformation: D?
) : Scope<K>(value)