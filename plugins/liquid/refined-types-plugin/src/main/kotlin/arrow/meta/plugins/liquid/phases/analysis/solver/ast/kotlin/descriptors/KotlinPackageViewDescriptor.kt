package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.PackageViewDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName

class KotlinPackageViewDescriptor(
  val impl: org.jetbrains.kotlin.descriptors.PackageViewDescriptor
) :
  PackageViewDescriptor, KotlinDeclarationDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.PackageViewDescriptor = impl

  override fun getMemberScope(): MemberScope = KotlinMemberScope { impl().memberScope }

  override val fqName: FqName
    get() = FqName(impl().fqName.asString())
}
