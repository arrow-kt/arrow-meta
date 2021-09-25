
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.psi.KtBinaryExpression

fun interface KotlinBinaryExpression: BinaryExpression, KotlinOperationExpression {
  override fun impl(): KtBinaryExpression
  override val operationToken: String
    get() = impl().operationToken.toFirOperation().operator
  override val left: Expression?
    get() = impl().left?.model()
  override val right: Expression?
    get() = impl().right?.model()
}
