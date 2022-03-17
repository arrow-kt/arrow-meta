@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.types.JavaTypeParameterConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Variance
import javax.lang.model.element.TypeParameterElement

public class JavaTypeParameterDescriptor(
  private val ctx: AnalysisContext,
  private val impl: TypeParameterElement
) : TypeParameterDescriptor, JavaMemberDescriptor(ctx, impl) {

  override val upperBounds: List<Type>
    get() = impl.bounds.map { it.model(ctx) }
  override val index: Int
    get() = impl.enclosingElement.typeParametersFromEverywhere.indexOf(impl)

  override val isReified: Boolean = false
  override val variance: Variance = Variance.Invariant
  override val isCapturedFromOuterDeclaration: Boolean = false

  override val typeConstructor: TypeConstructor
    get() = JavaTypeParameterConstructor(ctx, impl)
  override val defaultType: Type
    get() = impl.asType().model(ctx)
}
