package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor

fun interface KotlinParameterDescriptor : ParameterDescriptor, KotlinValueDescriptor {
  override fun impl() : org.jetbrains.kotlin.descriptors.ParameterDescriptor
}
