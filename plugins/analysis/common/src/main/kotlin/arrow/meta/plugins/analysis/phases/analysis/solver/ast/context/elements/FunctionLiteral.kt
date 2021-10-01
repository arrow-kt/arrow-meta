package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface FunctionLiteral : Function {
  fun hasParameterSpecification(): Boolean
  override val bodyExpression: Expression?
}
