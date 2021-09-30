package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.OperationReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

fun interface KotlinOperationReferenceExpression : OperationReferenceExpression, KotlinSimpleNameExpression {
  override fun impl(): KtOperationReferenceExpression
  override fun isConventionOperator(): Boolean =
    impl().isConventionOperator()
}
