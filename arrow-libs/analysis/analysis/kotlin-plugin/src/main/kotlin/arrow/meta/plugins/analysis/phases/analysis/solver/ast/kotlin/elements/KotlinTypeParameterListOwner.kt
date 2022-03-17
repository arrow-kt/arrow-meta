package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraintList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameterListOwner
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner

fun interface KotlinTypeParameterListOwner : TypeParameterListOwner, KotlinNamedDeclaration {
  override fun impl(): KtTypeParameterListOwner
  override val typeParameterList: TypeParameterList?
    get() = impl().typeParameterList?.model()
  override val typeConstraintList: TypeConstraintList?
    get() = impl().typeConstraintList?.model()
  override val typeConstraints: List<TypeConstraint>
    get() = impl().typeConstraints.map { it.model() }
  override val typeParameters: List<TypeParameter>
    get() = impl().typeParameters.map { it.model() }
}
