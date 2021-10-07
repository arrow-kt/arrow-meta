package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PackageFragmentDescriptor

class KotlinPackageFragmentDescriptor(
  val impl: org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
) :
  PackageFragmentDescriptor,
  KotlinDeclarationDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor = impl
}
