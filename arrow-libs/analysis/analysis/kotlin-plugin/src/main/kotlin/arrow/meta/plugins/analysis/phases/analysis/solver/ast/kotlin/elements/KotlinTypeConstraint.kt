package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeConstraint

class KotlinTypeConstraint(val impl: KtTypeConstraint) : TypeConstraint, KotlinElement {
  override fun impl(): KtTypeConstraint = impl
  override val subjectTypeParameterName: SimpleNameExpression?
    get() = impl().subjectTypeParameterName?.model()
  override val boundTypeReference: TypeReference?
    get() = impl().boundTypeReference?.model()
}
