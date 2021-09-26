package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeConstraintList
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeConstraintList

fun interface KotlinTypeConstraintList: TypeConstraintList {
 fun impl(): KtTypeConstraintList
  override val constraints: List<TypeConstraint>
    get() = impl().constraints.map { it.model() }
}
