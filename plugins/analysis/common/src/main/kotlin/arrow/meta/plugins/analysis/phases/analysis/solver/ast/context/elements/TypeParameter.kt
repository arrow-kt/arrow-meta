package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Variance

interface TypeParameter {
  val variance: Variance
  val extendsBound: TypeReference?
}
