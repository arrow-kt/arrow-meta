@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import com.sun.source.tree.BlockTree
import com.sun.source.tree.EmptyStatementTree
import com.sun.source.tree.ExpressionStatementTree
import com.sun.source.tree.Tree

public open class JavaBlockParent(
  private val ctx: AnalysisContext,
  private val elements: List<Tree>,
  owner: Tree
) : BlockExpression, JavaElement(ctx, owner) {
  override val statements: List<Expression>
    get() = elements.map { it.model(ctx) }
  override val firstStatement: Expression?
    get() = statements.firstOrNull()
  override val implicitReturnFromLast: Boolean = false
}

public class JavaBlock(ctx: AnalysisContext, impl: BlockTree) :
  BlockExpression, JavaBlockParent(ctx, impl.statements, impl) {}

public class JavaSingleBlock(ctx: AnalysisContext, impl: ExpressionStatementTree) :
  BlockExpression, JavaBlockParent(ctx, listOf(impl.expression), impl) {}

public class JavaEmptyBlock(ctx: AnalysisContext, impl: EmptyStatementTree) :
  BlockExpression, JavaBlockParent(ctx, emptyList(), impl) {}
