
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtBinaryExpression

class KotlinBinaryExpression(val impl: KtBinaryExpression) : BinaryExpression, KotlinOperationExpression {
  override fun impl(): KtBinaryExpression = impl
  override val operationToken: String
    get() = (impl().operationToken as KtSingleValueToken).value
  override val operationTokenRpr: String
    get() = impl().operationToken.toString()
  override val left: Expression?
    get() = impl().left?.model()
  override val right: Expression?
    get() = impl().right?.model()
}
