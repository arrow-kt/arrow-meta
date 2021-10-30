package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FunctionLiteral
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtLambdaExpression

class KotlinLambdaExpression(val impl: KtLambdaExpression) : LambdaExpression, KotlinExpression {
  override fun impl(): KtLambdaExpression = impl
  override val functionLiteral: FunctionLiteral
    get() = impl().functionLiteral.model()
  override val valueParameters: List<Parameter>
    get() = impl().valueParameters.map { it.model() }
  override val bodyExpression: BlockExpression?
    get() = impl().bodyExpression?.model()

  override fun hasDeclaredReturnType(): Boolean = impl().hasDeclaredReturnType()
}
