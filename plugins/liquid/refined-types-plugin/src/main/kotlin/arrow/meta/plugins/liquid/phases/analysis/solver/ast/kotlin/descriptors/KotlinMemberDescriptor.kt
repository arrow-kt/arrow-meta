package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.MemberDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Modality
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.Modality.ABSTRACT
import org.jetbrains.kotlin.descriptors.Modality.FINAL
import org.jetbrains.kotlin.descriptors.Modality.OPEN
import org.jetbrains.kotlin.descriptors.Modality.SEALED

fun interface KotlinMemberDescriptor : MemberDescriptor, KotlinDeclarationDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.MemberDescriptor

  override val modality: Modality
    get() = when (impl().modality) {
      FINAL -> Modality.FINAL
      SEALED -> Modality.SEALED
      OPEN -> Modality.OPEN
      ABSTRACT -> Modality.ABSTRACT
    }

  override val visibility: Visibility
    get() = KotlinVisibility { impl().visibility.delegate }

  override val isExpect: Boolean
    get() = impl().isExpect

  override val isActual: Boolean
    get() = impl().isActual

  override val isExternal: Boolean
    get() = impl().isExternal
}
