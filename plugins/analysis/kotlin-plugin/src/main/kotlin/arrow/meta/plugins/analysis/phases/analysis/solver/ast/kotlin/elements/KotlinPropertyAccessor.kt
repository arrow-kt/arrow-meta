package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PropertyAccessor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtPropertyAccessor

fun interface KotlinPropertyAccessor :
  PropertyAccessor,
  KotlinDeclarationWithBody,
  KotlinModifierListOwner,
  KotlinDeclarationWithInitializer {
  override fun impl(): KtPropertyAccessor
  override val isSetter: Boolean
    get() = impl().isSetter
  override val isGetter: Boolean
    get() = impl().isGetter
  override val parameterList: ParameterList?
    get() = impl().parameterList?.model()
  override val parameter: Parameter?
    get() = impl().parameter?.model()
  override val returnTypeReference: TypeReference?
    get() = impl().returnTypeReference?.model()
  override val property: Property
    get() = impl().property.model()
}
