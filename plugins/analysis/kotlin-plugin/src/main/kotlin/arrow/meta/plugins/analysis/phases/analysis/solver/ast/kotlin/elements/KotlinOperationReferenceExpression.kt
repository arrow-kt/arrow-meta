package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.OperationReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

class KotlinOperationReferenceExpression(val impl: KtOperationReferenceExpression) :
  OperationReferenceExpression, KotlinSimpleNameExpression {
  override fun impl(): KtOperationReferenceExpression = impl
  override fun isConventionOperator(): Boolean = impl().isConventionOperator()
}
