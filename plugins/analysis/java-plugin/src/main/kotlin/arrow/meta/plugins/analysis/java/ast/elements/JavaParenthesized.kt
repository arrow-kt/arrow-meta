@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParenthesizedExpression
import com.sun.source.tree.ParenthesizedTree

public class JavaParenthesized(ctx: AnalysisContext, impl: ParenthesizedTree) :
  ParenthesizedExpression, JavaElement(ctx, impl) {
  override val expression: Expression = impl.expression.model(ctx)
}
