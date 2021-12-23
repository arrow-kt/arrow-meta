package arrow.meta.plugins.analysis.phases.analysis.solver.collect.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor

data class DeclarationConstraints(
  val descriptor: DeclarationDescriptor,
  val pre: List<NamedConstraint>,
  val post: List<NamedConstraint>,
  val doNotLookAtArgumentsWhen: List<NamedConstraint>
)
