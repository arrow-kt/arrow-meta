@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DestructuringDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DoWhileExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ForExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ThreePieceForExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhileExpression
import com.sun.source.tree.DoWhileLoopTree
import com.sun.source.tree.EnhancedForLoopTree
import com.sun.source.tree.ForLoopTree
import com.sun.source.tree.WhileLoopTree

public class JavaDoWhile(private val ctx: AnalysisContext, private val impl: DoWhileLoopTree) :
  DoWhileExpression, JavaElement(ctx, impl) {
  override val condition: Expression
    get() = impl.condition.model(ctx)
  override val body: Expression
    get() = impl.statement.model(ctx)
}

public class JavaWhile(private val ctx: AnalysisContext, private val impl: WhileLoopTree) :
  WhileExpression, JavaElement(ctx, impl) {
  override val condition: Expression
    get() = impl.condition.model(ctx)
  override val body: Expression
    get() = impl.statement.model(ctx)
}

public class JavaEnhancedFor(
  private val ctx: AnalysisContext,
  private val impl: EnhancedForLoopTree
) : ForExpression, JavaElement(ctx, impl) {
  override val loopParameter: Parameter
    get() = JavaParameter(ctx, impl.variable, null)
  override val destructuringDeclaration: DestructuringDeclaration?
    get() = null
  override val loopRange: Expression
    get() = impl.expression.model(ctx)
  override val body: Expression
    get() = impl.statement.model(ctx)
}

public class JavaFor(private val ctx: AnalysisContext, private val impl: ForLoopTree) :
  ThreePieceForExpression, JavaElement(ctx, impl) {
  override val initializer: List<Expression>
    get() = impl.initializer.map { it.model(ctx) }
  override val condition: Expression
    get() = impl.condition.model(ctx)
  override val update: List<Expression>
    get() = impl.update.map { it.model(ctx) }
  override val body: Expression
    get() = impl.statement.model(ctx)
}
