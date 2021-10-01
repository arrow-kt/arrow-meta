package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FieldDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

fun interface KotlinFieldDescriptor : FieldDescriptor, KotlinAnnotated {
  override fun impl(): org.jetbrains.kotlin.descriptors.FieldDescriptor

  override val correspondingProperty: PropertyDescriptor
    get() =
      impl().correspondingProperty.model()
}
