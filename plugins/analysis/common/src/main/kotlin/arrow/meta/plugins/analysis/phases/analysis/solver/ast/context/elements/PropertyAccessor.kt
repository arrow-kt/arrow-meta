package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface PropertyAccessor : DeclarationWithBody, ModifierListOwner, DeclarationWithInitializer {
  val isSetter: Boolean
  val isGetter: Boolean
  val parameterList: ParameterList?
  val parameter: Parameter?
  val returnTypeReference: TypeReference?
  val property: Property
}
