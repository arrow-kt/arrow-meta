package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.PackageViewDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName

fun interface KotlinPackageViewDescriptor :
  PackageViewDescriptor, KotlinDeclarationDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.PackageViewDescriptor

  override val memberScope: MemberScope
    get() = KotlinMemberScope(impl().memberScope)

  override val fqName: FqName
    get() = FqName(impl().fqName.asString())
}

