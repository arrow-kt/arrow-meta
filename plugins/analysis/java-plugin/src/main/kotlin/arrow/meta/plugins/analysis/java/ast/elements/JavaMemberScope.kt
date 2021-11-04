@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

public class JavaMemberScope(
  private val ctx: AnalysisContext,
  private val enclosedElements: Collection<Element>
) : MemberScope {
  override fun getClassifierNames(): Set<Name> = getNames<TypeElement>()
  override fun getFunctionNames(): Set<Name> = getNames<ExecutableElement>()
  override fun getVariableNames(): Set<Name> = getNames<VariableElement>()

  private inline fun <reified A : Element> getNames(): Set<Name> =
    enclosedElements.filterIsInstance<A>().map { it.simpleName.name() }.toSet()

  override fun getContributedDescriptors(
    filter: (name: String) -> Boolean
  ): List<DeclarationDescriptor> =
    enclosedElements.filter { filter(it.simpleName.toString()) }.map { it.model(ctx) }
}
