package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ExpressionLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaArgument

class KotlinExpressionLambdaArgument(override val impl: KtLambdaArgument) : ExpressionLambdaArgument, KotlinLambdaArgument(impl) {
  override fun impl(): KtLambdaArgument = impl
}
