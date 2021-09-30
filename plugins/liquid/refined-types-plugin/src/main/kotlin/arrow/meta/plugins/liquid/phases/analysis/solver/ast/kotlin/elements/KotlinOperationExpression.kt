package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.OperationExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtOperationExpression

fun interface KotlinOperationExpression : OperationExpression, KotlinExpression {
  override fun impl(): KtOperationExpression
  override val operationReference: SimpleNameExpression
    get() = impl().operationReference.model()
}
