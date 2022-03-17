package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface ReceiverValue {
  val type: Type
  val isClassReceiver: Boolean
}
