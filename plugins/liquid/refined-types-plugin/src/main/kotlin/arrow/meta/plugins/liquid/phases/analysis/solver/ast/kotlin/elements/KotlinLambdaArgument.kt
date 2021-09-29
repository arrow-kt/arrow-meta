package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.LambdaArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtLambdaArgument

open class KotlinLambdaArgument(override val impl: KtLambdaArgument) : LambdaArgument, KotlinExpressionValueArgument(impl) {
  override fun impl(): KtLambdaArgument = impl
  override fun getLambdaExpression(): LambdaExpression? =
    impl().getLambdaExpression()?.model()
}
