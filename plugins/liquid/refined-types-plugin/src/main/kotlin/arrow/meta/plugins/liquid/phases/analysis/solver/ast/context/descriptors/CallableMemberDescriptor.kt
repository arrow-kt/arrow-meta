package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

interface CallableMemberDescriptor : CallableDescriptor, MemberDescriptor {
  val kind: Kind

  enum class Kind {
    DECLARATION, FAKE_OVERRIDE, DELEGATION, SYNTHESIZED;
  }
}
