@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

public class JavaFakeReference(private val impl: String, private val parent: Element) :
  SimpleNameExpression {
  override fun getReferencedName(): String = impl
  override fun getReferencedNameAsName(): Name = Name(impl)

  override fun type(context: ResolutionContext): Type? = null
  override fun lastBlockStatementOrThis(): Expression = this

  override val text: String = impl
  override fun impl(): Any = impl

  override fun getResolvedCall(context: ResolutionContext): ResolvedCall? = null
  override fun getVariableDescriptor(context: ResolutionContext): VariableDescriptor? = null
  override fun parents(): List<Element> = listOf(parent) + parent.parents()
  override fun location(): CompilerMessageSourceLocation? = null
  override val psiOrParent: Element
    get() = this
}
