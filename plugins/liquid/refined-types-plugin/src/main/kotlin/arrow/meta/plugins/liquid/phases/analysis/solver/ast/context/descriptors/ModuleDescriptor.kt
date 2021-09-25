package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name

interface ModuleDescriptor : DeclarationDescriptor {
  fun getPackage(pck: String): PackageViewDescriptor?
  fun getSubPackagesOf(fqName: FqName): List<FqName>

  val stableName: Name?
}


