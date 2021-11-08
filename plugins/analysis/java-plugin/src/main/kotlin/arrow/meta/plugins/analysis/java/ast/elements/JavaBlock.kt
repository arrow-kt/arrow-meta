@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import com.sun.source.tree.BlockTree
import com.sun.source.tree.EmptyStatementTree
import com.sun.source.tree.ExpressionStatementTree

public class JavaBlock(private val ctx: AnalysisContext, private val impl: BlockTree) :
  BlockExpression, JavaElement(ctx, impl) {
  override val statements: List<Expression>
    get() = impl.statements.map { it.model(ctx) }
  override val firstStatement: Expression?
    get() = statements.firstOrNull()
  override val implicitReturnFromLast: Boolean = false
}

public class JavaSingleBlock(
  private val ctx: AnalysisContext,
  private val impl: ExpressionStatementTree
) : BlockExpression, JavaElement(ctx, impl) {
  override val firstStatement: Expression
    get() = impl.expression.model(ctx)
  override val statements: List<Expression>
    get() = listOf(firstStatement)
  override val implicitReturnFromLast: Boolean = false
}

public class JavaEmptyBlock(ctx: AnalysisContext, impl: EmptyStatementTree) :
  BlockExpression, JavaElement(ctx, impl) {
  override val firstStatement: Expression? = null
  override val statements: List<Expression> = emptyList()
  override val implicitReturnFromLast: Boolean = false
}
