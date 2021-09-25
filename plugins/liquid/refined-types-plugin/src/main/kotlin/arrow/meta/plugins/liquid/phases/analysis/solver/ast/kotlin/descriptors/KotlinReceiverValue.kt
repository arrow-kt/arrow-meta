package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinReceiverValue :
  ReceiverValue {

  fun impl(): org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

  override val type: Type
    get() = KotlinType(impl().type)
}
