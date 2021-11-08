@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PackageFragmentDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PackageViewDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import javax.lang.model.element.PackageElement

public class JavaPackageDescriptor(
  private val ctx: AnalysisContext,
  private val impl: PackageElement
) : PackageViewDescriptor, PackageFragmentDescriptor, JavaDescriptor(ctx, impl) {
  override val fqName: FqName
    get() = FqName(impl.fqName)
  override val memberScope: MemberScope
    get() = JavaMemberScope(ctx, impl.enclosedElements)
}
