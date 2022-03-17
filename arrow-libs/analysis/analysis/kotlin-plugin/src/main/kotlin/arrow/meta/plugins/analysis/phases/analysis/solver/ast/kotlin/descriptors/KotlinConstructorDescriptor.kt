package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

open class KotlinConstructorDescriptor(
  override val impl: org.jetbrains.kotlin.descriptors.ConstructorDescriptor
) : ConstructorDescriptor, KotlinFunctionDescriptor(impl) {
  override fun impl(): org.jetbrains.kotlin.descriptors.ConstructorDescriptor = impl

  override val constructedClass: ClassDescriptor
    get() = impl().constructedClass.model()

  override val isPrimary: Boolean
    get() = impl().isPrimary
}
