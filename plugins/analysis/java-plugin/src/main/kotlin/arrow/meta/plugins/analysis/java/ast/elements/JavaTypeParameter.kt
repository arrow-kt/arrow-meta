@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Variance
import com.sun.source.tree.TypeParameterTree

public class JavaTypeParameter(ctx: AnalysisContext, impl: TypeParameterTree) : TypeParameter {
  override val variance: Variance = Variance.Invariant
  override val extendsBounds: List<TypeReference> = impl.bounds.map { it.model(ctx) }
}
