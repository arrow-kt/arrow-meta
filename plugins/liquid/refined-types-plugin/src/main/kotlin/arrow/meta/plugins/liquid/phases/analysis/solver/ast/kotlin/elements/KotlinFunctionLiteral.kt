package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FunctionLiteral
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtFunctionLiteral

fun interface KotlinFunctionLiteral : FunctionLiteral, KotlinFunction {
  override fun impl(): KtFunctionLiteral
  override fun hasParameterSpecification(): Boolean =
    impl().hasParameterSpecification()

  override val bodyExpression: Expression?
    get() = impl().bodyExpression?.model()
}
