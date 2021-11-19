package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberScope
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter

fun interface KotlinMemberScope : MemberScope {

  fun impl(): org.jetbrains.kotlin.resolve.scopes.MemberScope

  override fun getClassifierNames(): Set<Name> =
    impl().getClassifierNames()?.map { Name(it.asString()) }.orEmpty().toSet()

  override fun getFunctionNames(): Set<Name> =
    impl().getFunctionNames().map { Name(it.asString()) }.toSet()

  override fun getVariableNames(): Set<Name> =
    impl().getVariableNames().map { Name(it.asString()) }.toSet()

  override fun getContributedDescriptors(
    filter: (name: String) -> Boolean
  ): List<DeclarationDescriptor> =
    impl().getContributedDescriptors(DescriptorKindFilter.ALL) { filter(it.toString()) }.map {
      it.model()
    }
}
