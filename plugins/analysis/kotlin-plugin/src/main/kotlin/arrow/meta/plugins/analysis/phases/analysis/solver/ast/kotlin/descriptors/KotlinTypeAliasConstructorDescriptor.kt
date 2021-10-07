package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeAliasConstructorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeAliasDescriptor

class KotlinTypeAliasConstructorDescriptor(override val impl: org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor) : KotlinConstructorDescriptor(impl), TypeAliasConstructorDescriptor {
  override fun impl(): org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor = impl

  override val underlyingConstructorDescriptor: ConstructorDescriptor
    get() = KotlinConstructorDescriptor(impl.underlyingConstructorDescriptor)

  override fun getContainingDeclaration(): TypeAliasDescriptor =
    KotlinTypeAliasDescriptor(impl.containingDeclaration)
}
