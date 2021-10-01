package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface CallableDeclaration : NamedDeclaration, TypeParameterListOwner {
  val valueParameterList: ParameterList?
  val valueParameters: List<Parameter>
  val receiverTypeReference: TypeReference?
  val typeReference: TypeReference?
}
