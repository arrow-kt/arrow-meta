package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName

interface PackageViewDescriptor : DeclarationDescriptor {
  fun getMemberScope(): MemberScope
  val fqName: FqName
}
