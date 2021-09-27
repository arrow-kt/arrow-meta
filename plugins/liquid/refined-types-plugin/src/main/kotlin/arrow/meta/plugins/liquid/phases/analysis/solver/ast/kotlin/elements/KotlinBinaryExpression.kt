
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtBinaryExpression

class KotlinBinaryExpression(val impl: KtBinaryExpression): BinaryExpression, KotlinOperationExpression {
  override fun impl(): KtBinaryExpression = impl
  override val operationToken: String
    get() = (impl().operationToken as KtSingleValueToken).value
  override val left: Expression?
    get() = impl().left?.model()
  override val right: Expression?
    get() = impl().right?.model()
}
