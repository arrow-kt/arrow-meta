@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import com.sun.source.tree.BlockTree
import com.sun.source.tree.ExpressionStatementTree

public class JavaBlock(ctx: AnalysisContext, impl: BlockTree) :
  BlockExpression, JavaElement(ctx, impl) {
  override val statements: List<Expression> = impl.statements.map { it.model(ctx) }
  override val firstStatement: Expression? = statements.firstOrNull()
}

public class JavaSingleBlock(ctx: AnalysisContext, impl: ExpressionStatementTree) :
  BlockExpression, JavaElement(ctx, impl) {
  override val firstStatement: Expression = impl.expression.model(ctx)
  override val statements: List<Expression> = listOf(firstStatement)
}
