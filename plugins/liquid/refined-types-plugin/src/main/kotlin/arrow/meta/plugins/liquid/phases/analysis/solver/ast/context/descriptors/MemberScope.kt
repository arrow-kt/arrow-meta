package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name


interface MemberScope {
  fun getClassifierNames(): Set<Name>

  fun getFunctionNames(): Set<Name>

  fun getVariableNames(): Set<Name>

  fun getContributedDescriptors(filter: (name: String) -> Boolean): List<DeclarationDescriptor>

}


