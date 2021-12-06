package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PackageViewDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import java.io.File
import javax.lang.model.element.ModuleElement
import javax.lang.model.element.PackageElement

public class JavaModuleDescriptor(
  private val ctx: AnalysisContext,
  private val impl: ModuleElement
) : JavaDescriptor(ctx, impl), ModuleDescriptor {
  override fun getPackage(pck: String): PackageViewDescriptor? =
    impl
      .enclosedElements
      .filterIsInstance<PackageElement>()
      .firstOrNull { it.fqName == pck }
      ?.model(ctx)

  override fun getSubPackagesOf(fqName: FqName): List<FqName> =
    impl
      .enclosedElements
      .filterIsInstance<PackageElement>()
      .filter { it.fqName.startsWith(fqName.name + ".") }
      .map { it.model(ctx) }

  override fun getBuildDirectory(): File =
    File(System.getProperty("arrow.meta.generate.source.dir"))

  override val stableName: Name = Name(impl.simpleName.toString())
}
