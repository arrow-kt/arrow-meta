@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LabeledExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import com.sun.source.tree.LabeledStatementTree

public class JavaLabeled(private val ctx: AnalysisContext, private val impl: LabeledStatementTree) :
  LabeledExpression, JavaElement(ctx, impl) {
  override val baseExpression: Expression
    get() = impl.statement.model(ctx)

  override fun getTargetLabel(): SimpleNameExpression? = null

  override fun getLabelName(): String? = impl.label?.toString()
  override fun getLabelNameAsName(): Name? = impl.label?.name()
}
