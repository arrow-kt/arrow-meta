package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface ValueParameterDescriptor : VariableDescriptor, ParameterDescriptor {
  val index: Int
  val isCrossinline: Boolean
  val isNoinline: Boolean
  val varargElementType: Type?
  fun declaresDefaultValue(): Boolean
  val defaultValue: Expression?
}
