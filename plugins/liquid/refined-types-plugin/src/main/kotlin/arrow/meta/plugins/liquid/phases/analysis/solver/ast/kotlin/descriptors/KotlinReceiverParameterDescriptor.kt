package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverValue

fun interface KotlinReceiverParameterDescriptor : ReceiverParameterDescriptor, KotlinParameterDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor

  override val value: ReceiverValue
    get() = KotlinReceiverValue { impl().value }
}
