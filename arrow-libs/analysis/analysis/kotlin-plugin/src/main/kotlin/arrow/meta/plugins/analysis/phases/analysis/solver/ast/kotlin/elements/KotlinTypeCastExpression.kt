package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeCastExpresionKind
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeCastExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS

class KotlinTypeCastExpression(val impl: KtBinaryExpressionWithTypeRHS) :
  TypeCastExpression, KotlinOperationExpression {
  override fun impl(): KtBinaryExpressionWithTypeRHS = impl

  override val operationToken: String
    get() = this.operationReference.getReferencedName()
  override val left: Expression
    get() = impl.left.model()
  override val right: TypeReference?
    get() = impl.right?.model()
  override val kind: TypeCastExpresionKind
    get() =
      when (operationToken) {
        "as" -> TypeCastExpresionKind.POSITIVE_TYPE_CAST
        "as?" -> TypeCastExpresionKind.QUESTION_TYPE_CAST
        else -> throw IllegalArgumentException("type cast with wrong operator")
      }
}
