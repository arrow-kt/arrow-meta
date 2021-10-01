package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type

interface ClassifierDescriptor {
  val typeConstructor: TypeConstructor
  val defaultType: Type
}
