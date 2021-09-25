package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface PropertyAccessor : DeclarationWithBody, ModifierListOwner,
  DeclarationWithInitializer {
  val isSetter: Boolean
  val isGetter: Boolean
  val parameterList: ParameterList?
  val parameter: Parameter?
  val returnTypeReference: TypeReference?
  val property: Property
}
