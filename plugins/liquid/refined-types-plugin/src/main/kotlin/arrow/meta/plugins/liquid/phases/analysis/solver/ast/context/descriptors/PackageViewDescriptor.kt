package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName

interface PackageViewDescriptor : DeclarationDescriptor {
  val memberScope: MemberScope
  val fqName: FqName
}

