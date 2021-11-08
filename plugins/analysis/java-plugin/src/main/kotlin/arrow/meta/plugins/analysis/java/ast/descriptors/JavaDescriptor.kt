@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import javax.lang.model.element.Element

public open class JavaDescriptor(private val ctx: AnalysisContext, private val impl: Element) :
  DeclarationDescriptor {
  override fun impl(): Element = impl

  override val module: ModuleDescriptor
    get() = ctx.elements.getModuleOf(impl).model(ctx)
  override val containingDeclaration: DeclarationDescriptor
    get() = impl.enclosingElement.model(ctx)
  override val containingPackage: FqName
    get() = FqName(impl.enclosingElement.fqName)

  override val fqNameSafe: FqName = FqName(impl.fqName)
  override val name: Name = impl.simpleName.name()

  override fun element():
    arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element? =
    ctx.resolver.tree(impl)?.model(ctx)

  override fun annotations(): Annotations = JavaAnnotations(ctx, impl.annotationMirrors)
}
