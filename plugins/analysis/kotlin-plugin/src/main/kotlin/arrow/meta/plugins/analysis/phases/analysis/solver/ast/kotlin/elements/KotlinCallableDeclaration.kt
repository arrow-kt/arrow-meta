package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtCallableDeclaration

fun interface KotlinCallableDeclaration :
  CallableDeclaration, KotlinNamedDeclaration, KotlinTypeParameterListOwner {
  override fun impl(): KtCallableDeclaration
  override val valueParameterList: ParameterList?
    get() = impl().valueParameterList?.model()
  override val valueParameters: List<Parameter>
    get() = impl().valueParameters.map { it.model() }
  override val receiverTypeReference: TypeReference?
    get() = impl().receiverTypeReference?.model()
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
}
