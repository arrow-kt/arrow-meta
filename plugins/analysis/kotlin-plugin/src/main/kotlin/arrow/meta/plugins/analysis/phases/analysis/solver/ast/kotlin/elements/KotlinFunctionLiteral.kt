package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FunctionLiteral
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtFunctionLiteral

class KotlinFunctionLiteral(val impl: KtFunctionLiteral) : FunctionLiteral, KotlinFunction {
  override fun impl(): KtFunctionLiteral = impl
  override fun hasParameterSpecification(): Boolean = impl().hasParameterSpecification()

  override val bodyExpression: Expression?
    get() = impl().bodyExpression?.model()
}
