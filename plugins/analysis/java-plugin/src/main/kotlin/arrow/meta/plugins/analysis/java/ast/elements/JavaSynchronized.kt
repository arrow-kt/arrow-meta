@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SynchronizedExpression
import com.sun.source.tree.SynchronizedTree

public class JavaSynchronized(ctx: AnalysisContext, impl: SynchronizedTree) :
  SynchronizedExpression, JavaElement(ctx, impl) {
  override val subject: Expression = impl.expression.model(ctx)
  override val block: BlockExpression = impl.block.model(ctx)
}
