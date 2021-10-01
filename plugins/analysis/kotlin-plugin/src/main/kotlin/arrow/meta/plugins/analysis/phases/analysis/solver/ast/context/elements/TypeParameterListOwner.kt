
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeParameterListOwner : NamedDeclaration {
  val typeParameterList: TypeParameterList?
  val typeConstraintList: TypeConstraintList?
  val typeConstraints: List<TypeConstraint>
  val typeParameters: List<TypeParameter>
}
