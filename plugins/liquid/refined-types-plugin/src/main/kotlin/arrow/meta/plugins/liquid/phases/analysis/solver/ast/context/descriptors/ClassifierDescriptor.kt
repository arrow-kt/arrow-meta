package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type

interface ClassifierDescriptor {
  val typeConstructor: TypeConstructor
  val defaultType: Type
}
