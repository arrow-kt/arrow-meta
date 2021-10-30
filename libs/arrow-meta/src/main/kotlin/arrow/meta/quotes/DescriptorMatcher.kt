package arrow.meta.quotes

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtElement

inline fun <reified D : DeclarationDescriptor> List<DeclarationDescriptor>.descriptor(
  element: KtElement
): D? = this.firstOrNull { it is D && it.findPsi() == element } as? D
