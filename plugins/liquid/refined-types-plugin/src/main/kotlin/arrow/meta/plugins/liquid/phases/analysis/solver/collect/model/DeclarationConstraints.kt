package arrow.meta.plugins.liquid.phases.analysis.solver.collect.model

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

data class DeclarationConstraints(
  val descriptor: DeclarationDescriptor,
  val pre: List<NamedConstraint>,
  val post: List<NamedConstraint>
)
