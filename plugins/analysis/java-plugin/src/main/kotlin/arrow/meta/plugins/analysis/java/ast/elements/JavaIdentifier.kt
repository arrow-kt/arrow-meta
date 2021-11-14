@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NameReferenceExpression
import com.sun.source.tree.IdentifierTree

public class JavaIdentifier(ctx: AnalysisContext, private val impl: IdentifierTree) :
  NameReferenceExpression, JavaElement(ctx, impl) {
  override fun getReferencedName(): String = impl.name.toString()
  override fun getReferencedNameAsName(): Name = impl.name.name()
}
