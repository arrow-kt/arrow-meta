@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Annotation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotationEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierListOwner
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import com.sun.source.tree.BlockTree
import com.sun.source.tree.Tree
import com.sun.tools.javac.tree.JCTree

public open class JavaElement(private val ctx: AnalysisContext, private val impl: Tree) :
  Expression, ModifierListOwner {
  override fun impl(): Tree = impl

  override val text: String
    get() = impl.toString()

  override fun getResolvedCall(context: ResolutionContext): ResolvedCall? = impl.resolvedCall(ctx)

  override fun parents(): List<Element> =
    ctx.resolver.parentTrees(impl).mapNotNull { it.modelCautious(ctx) }

  override fun location(): CompilerMessageSourceLocation {
    val (start, end) = ctx.resolver.positionOf(impl)
    return JavaSourceLocation(ctx, start, end)
  }

  override val psiOrParent: Element = this

  public fun type(): Type? =
    when (impl) {
      is JCTree -> impl.type.modelCautious(ctx)
      else -> null
    }
  override fun type(context: ResolutionContext): Type? = type()

  override fun lastBlockStatementOrThis(): Expression =
    when (impl) {
      is BlockTree -> impl.statements.last().model(ctx)
      else -> this
    }

  override val modifierList: ModifierList? = null // TODO: fix later

  override fun getAnnotations(): List<Annotation> = modifierList?.annotations.orEmpty()
  override val annotationEntries: List<AnnotationEntry>
    get() = modifierList?.annotationEntries.orEmpty()
}
