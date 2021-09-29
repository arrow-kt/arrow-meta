package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassifierDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeConstructor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinClassifierDescriptor : ClassifierDescriptor {
  fun impl(): org.jetbrains.kotlin.descriptors.ClassifierDescriptor

  override val typeConstructor: TypeConstructor
    get() = KotlinTypeConstructor { impl().typeConstructor }
  override val defaultType: Type
    get() = KotlinType(impl().defaultType)
}
