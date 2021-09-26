package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeConstraint

fun interface KotlinTypeConstraint: TypeConstraint {
  fun impl(): KtTypeConstraint
  override val subjectTypeParameterName: SimpleNameExpression?
    get() = impl().subjectTypeParameterName?.model()
  override val boundTypeReference: TypeReference?
    get() = impl().boundTypeReference?.model()
}
