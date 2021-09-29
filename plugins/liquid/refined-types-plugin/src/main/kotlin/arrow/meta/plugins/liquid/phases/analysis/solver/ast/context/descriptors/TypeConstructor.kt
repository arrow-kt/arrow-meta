package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type

interface TypeConstructor {
  val parameters: List<TypeParameterDescriptor>
  val supertypes: Collection<Type>
  val isFinal: Boolean
  val isDenotable: Boolean
  val declarationDescriptor: ClassifierDescriptor?
}
