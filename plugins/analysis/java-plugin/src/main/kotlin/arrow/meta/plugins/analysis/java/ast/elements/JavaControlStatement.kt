@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BreakExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ContinueExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ReturnExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import com.sun.source.tree.BreakTree
import com.sun.source.tree.ContinueTree
import com.sun.source.tree.ReturnTree

public class JavaReturn(private val ctx: AnalysisContext, private val impl: ReturnTree) :
  ReturnExpression, JavaElement(ctx, impl) {
  override val returnedExpression: Expression?
    get() = impl.expression.model(ctx)

  // no return labels in Java
  override val labeledExpression: Expression? = null
  override fun getTargetLabel(): SimpleNameExpression? = null
  override fun getLabelName(): String? = null
  override fun getLabelNameAsName(): Name? = null
}

public class JavaContinue(ctx: AnalysisContext, private val impl: ContinueTree) :
  ContinueExpression, JavaElement(ctx, impl) {
  override fun getTargetLabel(): SimpleNameExpression = JavaReference(impl.label, this)
  override fun getLabelName(): String = impl.label.toString()
  override fun getLabelNameAsName(): Name = impl.label.name()
}

public class JavaBreak(ctx: AnalysisContext, private val impl: BreakTree) :
  BreakExpression, JavaElement(ctx, impl) {
  override fun getTargetLabel(): SimpleNameExpression = JavaReference(impl.label, this)
  override fun getLabelName(): String = impl.label.toString()
  override fun getLabelNameAsName(): Name = impl.label.name()
}
