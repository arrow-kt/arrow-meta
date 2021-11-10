@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.element.Name

public class JavaFakeReference(private val impl: Name, private val parent: Element) :
  SimpleNameExpression {
  override fun getReferencedName(): String = impl.toString()
  override fun getReferencedNameAsName():
    arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name = impl.name()

  override fun type(context: ResolutionContext): Type? = null
  override fun lastBlockStatementOrThis(): Expression = this

  override val text: String
    get() = impl.toString()
  override fun impl(): Any = impl

  override fun getResolvedCall(context: ResolutionContext): ResolvedCall? = null
  override fun parents(): List<Element> = listOf(parent) + parent.parents()
  override fun location(): CompilerMessageSourceLocation? = null
  override val psiOrParent: Element
    get() = this
}
