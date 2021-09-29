package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface CallableDeclaration : NamedDeclaration, TypeParameterListOwner {
  val valueParameterList: ParameterList?
  val valueParameters: List<Parameter>
  val receiverTypeReference: TypeReference?
  val typeReference: TypeReference?
}
