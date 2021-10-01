
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface CallElement : Element {
  val calleeExpression: Expression?
  val valueArguments: List<ValueArgument>
  val lambdaArguments: List<ExpressionLambdaArgument>
  val typeArguments: List<TypeProjection>
}
