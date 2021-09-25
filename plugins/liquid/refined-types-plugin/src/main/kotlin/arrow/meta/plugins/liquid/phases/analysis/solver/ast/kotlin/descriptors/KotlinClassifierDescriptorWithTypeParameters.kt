package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassifierDescriptorWithTypeParameters
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Visibility
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model

fun interface KotlinClassifierDescriptorWithTypeParameters :
  ClassifierDescriptorWithTypeParameters,
  KotlinClassifierDescriptor,
  KotlinDeclarationDescriptorWithVisibility,
  KotlinMemberDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.ClassifierDescriptorWithTypeParameters

  override val isInner: Boolean
    get() = impl().isInner

  override val declaredTypeParameters: List<TypeParameterDescriptor>
    get() = impl().declaredTypeParameters.map { it.model() }

  override val visibility: Visibility
    get() = KotlinVisibility(impl().visibility.delegate)
}
