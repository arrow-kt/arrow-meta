package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name

interface MemberScope {
  fun getClassifierNames(): Set<Name>

  fun getFunctionNames(): Set<Name>

  fun getVariableNames(): Set<Name>

  fun getContributedDescriptors(filter: (name: String) -> Boolean): List<DeclarationDescriptor>
}
