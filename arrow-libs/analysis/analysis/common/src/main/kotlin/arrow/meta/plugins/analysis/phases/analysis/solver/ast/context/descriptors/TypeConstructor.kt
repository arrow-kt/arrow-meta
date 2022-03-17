package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface TypeConstructor {
  val parameters: List<TypeParameterDescriptor>
  val supertypes: Collection<Type>
  val isFinal: Boolean
  val isDenotable: Boolean
  val declarationDescriptor: ClassifierDescriptor?
}
