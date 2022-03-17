package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface LambdaExpression : Expression {
  val functionLiteral: FunctionLiteral
  val valueParameters: List<Parameter>
  val bodyExpression: Expression?
  fun hasDeclaredReturnType(): Boolean
}
