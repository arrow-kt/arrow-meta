@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.IfExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenCondition
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenExpression
import com.sun.source.tree.CaseTree
import com.sun.source.tree.ConditionalExpressionTree
import com.sun.source.tree.ExpressionTree
import com.sun.source.tree.IfTree
import com.sun.source.tree.SwitchTree
import com.sun.source.tree.Tree

public abstract class JavaConditional(
  private val ctx: AnalysisContext,
  impl: Tree,
  private val conditionTree: ExpressionTree,
  private val thenTree: Tree,
  private val elseTree: Tree?
) : IfExpression, JavaElement(ctx, impl) {
  override val condition: Expression?
    get() = conditionTree.model(ctx)
  override val thenExpression: Expression?
    get() = thenTree.model(ctx)
  override val elseExpression: Expression?
    get() = elseTree?.model(ctx)
}

public class JavaIf(ctx: AnalysisContext, impl: IfTree) :
  JavaConditional(ctx, impl, impl.condition, impl.thenStatement, impl.elseStatement) {}

public class JavaTernaryConditional(ctx: AnalysisContext, impl: ConditionalExpressionTree) :
  JavaConditional(ctx, impl, impl.condition, impl.trueExpression, impl.falseExpression) {}

public class JavaSwitch(private val ctx: AnalysisContext, private val impl: SwitchTree) :
  WhenExpression, JavaElement(ctx, impl) {
  // no subject variables in Java
  override val subjectVariable: Property? = null
  override val subjectExpression: Expression
    get() = impl.expression.model(ctx)
  override val entries: List<WhenEntry>
    get() = impl.cases.map { it.model(ctx) }
  override val elseExpression: Expression?
    get() =
      impl.cases.firstOrNull { it.expression == null }?.model<CaseTree, JavaCase>(ctx)?.expression
}

public class JavaCase(private val ctx: AnalysisContext, private val impl: CaseTree) :
  WhenEntry, JavaElement(ctx, impl) {
  override val isElse: Boolean
    get() = impl.expression == null
  override val conditions: List<WhenCondition>
    get() = listOfNotNull(impl.expression?.model(ctx))
  override val expression: Expression
    get() = JavaBlockParent(ctx, impl.statements, impl)
}
