@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstantExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NullExpression
import com.sun.source.tree.LiteralTree

public class JavaLiteral(ctx: AnalysisContext, impl: LiteralTree) :
  ConstantExpression, JavaElement(ctx, impl) {
  init {
    require(impl.value != null)
  }
}

public class JavaNull(ctx: AnalysisContext, impl: LiteralTree) :
  NullExpression, JavaElement(ctx, impl) {
  init {
    require(impl.value == null)
  }
}
