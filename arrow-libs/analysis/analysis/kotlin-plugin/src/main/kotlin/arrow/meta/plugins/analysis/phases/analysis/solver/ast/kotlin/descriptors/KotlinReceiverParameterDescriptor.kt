package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverValue

class KotlinReceiverParameterDescriptor(
  val impl: org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
) : ReceiverParameterDescriptor, KotlinParameterDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor = impl

  override val value: ReceiverValue
    get() = KotlinReceiverValue(impl().value)
}
