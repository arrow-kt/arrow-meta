package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model


fun interface KotlinConstructorDescriptor : ConstructorDescriptor, KotlinFunctionDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.ConstructorDescriptor

  override val constructedClass: ClassDescriptor
    get() = impl().constructedClass.model()

  override val isPrimary: Boolean
    get() = impl().isPrimary
}
