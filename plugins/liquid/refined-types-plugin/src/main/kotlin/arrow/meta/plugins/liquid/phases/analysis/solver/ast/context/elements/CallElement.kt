
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface CallElement : Element {
  val calleeExpression: Expression?
  val valueArguments: List<ValueArgument>
  val lambdaArguments: List<ExpressionLambdaArgument>
  val typeArguments: List<TypeProjection>
}
