package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Variance
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type

interface TypeParameterDescriptor : ClassifierDescriptor {
  val isReified: Boolean
  val variance: Variance
  val upperBounds: List<Type>

  val index: Int
  val isCapturedFromOuterDeclaration: Boolean
}
