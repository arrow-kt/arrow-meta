@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ProjectionKind
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeProjection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import com.sun.source.tree.Tree

public class JavaTypeProjection(private val ctx: AnalysisContext, private val impl: Tree)
  : TypeProjection {
  override val projectionKind: ProjectionKind = when (impl.kind) {
    Tree.Kind.EXTENDS_WILDCARD -> ProjectionKind.OUT
    Tree.Kind.SUPER_WILDCARD -> ProjectionKind.IN
    Tree.Kind.UNBOUNDED_WILDCARD -> ProjectionKind.STAR
    else -> ProjectionKind.NONE
  }
  override val typeReference: TypeReference?
    get() = JavaTypeReference(ctx, impl)
}
