package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.resolve.scopes.receivers.ClassValueReceiver

class KotlinReceiverValue(val impl: org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue) :
  ReceiverValue {
  fun impl(): org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue = impl
  override val type: Type
    get() = KotlinType(impl().type)
  override val isClassReceiver: Boolean
    get() = impl is ClassValueReceiver
}
