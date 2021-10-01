package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSuperExpression

interface KotlinSuperExpression : SuperExpression, KotlinInstanceExpressionWithLabel {
  override fun impl(): KtSuperExpression
  override val superTypeQualifier: TypeReference?
    get() = impl().superTypeQualifier?.model()
}
