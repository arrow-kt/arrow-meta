package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinLambdaExpression : KotlinExpression {
  val functionLiteral: KotlinFunctionLiteral
  val valueParameters: List<KotlinParameter>
  val bodyExpression: KotlinBlockExpression?
  fun hasDeclaredReturnType(): Boolean
}
