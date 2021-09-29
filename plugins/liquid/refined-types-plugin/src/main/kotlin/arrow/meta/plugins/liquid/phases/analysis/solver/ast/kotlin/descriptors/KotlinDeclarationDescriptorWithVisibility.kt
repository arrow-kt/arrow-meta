package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptorWithVisibility
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Visibility

fun interface KotlinDeclarationDescriptorWithVisibility :
  DeclarationDescriptorWithVisibility,
  KotlinDeclarationDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
  override val visibility: Visibility
    get() = KotlinVisibility { impl().visibility.delegate }
}
