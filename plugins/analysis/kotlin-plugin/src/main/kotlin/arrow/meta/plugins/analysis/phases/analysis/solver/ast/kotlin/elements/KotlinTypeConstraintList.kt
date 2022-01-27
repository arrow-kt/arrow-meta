package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraintList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeConstraintList

class KotlinTypeConstraintList(val impl: KtTypeConstraintList) : TypeConstraintList, KotlinElement {
  override fun impl(): KtTypeConstraintList = impl
  override val constraints: List<TypeConstraint>
    get() = impl().constraints.map { it.model() }
}
