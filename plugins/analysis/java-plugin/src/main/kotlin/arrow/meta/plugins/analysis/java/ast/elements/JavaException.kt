@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CatchClause
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FinallySection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TryExpression
import com.sun.source.tree.CatchTree
import com.sun.source.tree.Tree
import com.sun.source.tree.TryTree

public open class JavaTry(private val ctx: AnalysisContext, private val impl: TryTree) :
  TryExpression, JavaElement(ctx, impl) {
  override val tryBlock: BlockExpression
    get() = impl.block.model(ctx)
  override val catchClauses: List<CatchClause>
    get() = TODO("Not yet implemented")
  override val finallyBlock: FinallySection?
    get() = impl.finallyBlock?.let { JavaFinally(ctx, it, impl) }
}

public open class JavaFinally(
  private val ctx: AnalysisContext,
  private val impl: Tree,
  private val owner: TryTree
) : FinallySection, JavaElement(ctx, impl) {
  override val finalExpression: BlockExpression
    get() =
      when (val final = impl.model<Tree, JavaElement>(ctx)) {
        is BlockExpression -> final
        else -> JavaBlockParent(ctx, listOf(impl), owner)
      }
}

public open class JavaCatch(private val ctx: AnalysisContext, private val impl: CatchTree) :
  CatchClause, JavaElement(ctx, impl) {
  override val catchParameter: Parameter
    get() = JavaParameter(ctx, impl.parameter, null)
  override val parameterList: ParameterList?
    get() =
      object : ParameterList {
        override val parameters: List<Parameter> = listOf(catchParameter)
        override val ownerFunction: DeclarationWithBody? = null
      }
  override val catchBody: Expression?
    get() = impl.block.model(ctx)
}
