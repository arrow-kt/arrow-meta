package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import java.io.File

interface ModuleDescriptor : DeclarationDescriptor {
  fun getPackage(pck: String): PackageViewDescriptor?
  fun getSubPackagesOf(fqName: FqName): List<FqName>
  fun getBuildDirectory(): File
  val stableName: Name?
}
