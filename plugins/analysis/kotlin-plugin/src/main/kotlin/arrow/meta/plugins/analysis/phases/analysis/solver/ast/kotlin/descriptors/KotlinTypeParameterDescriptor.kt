package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Variance
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance.INVARIANT
import org.jetbrains.kotlin.types.Variance.IN_VARIANCE
import org.jetbrains.kotlin.types.Variance.OUT_VARIANCE

fun interface KotlinTypeParameterDescriptor :
  TypeParameterDescriptor, KotlinClassifierDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.TypeParameterDescriptor

  override val isReified: Boolean
    get() = impl().isReified
  override val variance: Variance
    get() = when (impl().variance) {
      INVARIANT -> Variance.Invariant
      IN_VARIANCE -> Variance.In
      OUT_VARIANCE -> Variance.Out
    }
  override val upperBounds: List<Type>
    get() = impl().upperBounds.map { KotlinType(it) }
  override val index: Int
    get() = impl().index
  override val isCapturedFromOuterDeclaration: Boolean
    get() = impl().isCapturedFromOuterDeclaration
}
