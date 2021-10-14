package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name

interface MemberScope {
  fun getClassifierNames(): Set<Name>

  fun getFunctionNames(): Set<Name>

  fun getVariableNames(): Set<Name>

  fun getContributedDescriptors(filter: (name: String) -> Boolean): List<DeclarationDescriptor>
}

class CombinedMemberScope(private val scopes: Collection<MemberScope>): MemberScope {
  override fun getClassifierNames(): Set<Name> =
    scopes.flatMap { it.getClassifierNames() }.toSet()
  override fun getFunctionNames(): Set<Name> =
    scopes.flatMap { it.getFunctionNames() }.toSet()
  override fun getVariableNames(): Set<Name> =
    scopes.flatMap { it.getVariableNames() }.toSet()
  override fun getContributedDescriptors(
    filter: (name: String) -> Boolean
  ): List<DeclarationDescriptor> =
    scopes.flatMap { it.getContributedDescriptors(filter) }.distinctBy { it.impl() }
}
