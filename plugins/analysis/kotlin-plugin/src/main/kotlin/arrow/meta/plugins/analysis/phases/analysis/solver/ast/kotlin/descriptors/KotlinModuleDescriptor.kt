package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.phases.analysis.getOrCreateBaseDirectory
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PackageViewDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import java.io.File
import org.jetbrains.kotlin.config.CompilerConfiguration

class KotlinModuleDescriptor(
  val config: CompilerConfiguration?,
  val impl: org.jetbrains.kotlin.descriptors.ModuleDescriptor
) : ModuleDescriptor, KotlinDeclarationDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.ModuleDescriptor = impl

  override fun getPackage(pck: String): PackageViewDescriptor? =
    impl().getPackage(org.jetbrains.kotlin.name.FqName(pck)).model()

  override fun getSubPackagesOf(fqName: FqName): List<FqName> =
    impl().getSubPackagesOf(org.jetbrains.kotlin.name.FqName(fqName.name)) { true }.map {
      FqName(it.asString())
    }

  override fun getBuildDirectory(): File = getOrCreateBaseDirectory(config)

  override val stableName: Name?
    get() = impl().stableName?.let { Name(it.asString()) }
}
