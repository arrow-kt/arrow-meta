package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface LambdaExpression : Expression {
  val functionLiteral: FunctionLiteral
  val valueParameters: List<Parameter>
  val bodyExpression: BlockExpression?
  fun hasDeclaredReturnType(): Boolean
}
