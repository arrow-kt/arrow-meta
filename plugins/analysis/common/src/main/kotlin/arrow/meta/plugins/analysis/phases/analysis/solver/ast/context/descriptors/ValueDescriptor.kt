package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type

interface ValueDescriptor : CallableDescriptor {
  val type: Type
}
