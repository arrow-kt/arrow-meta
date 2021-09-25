package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ExpressionLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaArgument

fun interface KotlinExpressionLambdaArgument : ExpressionLambdaArgument, KotlinLambdaArgument {
  override fun impl(): KtLambdaArgument
}
