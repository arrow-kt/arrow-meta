package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind

class KotlinSimpleFunctionDescriptor(
  override val impl: org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
) : SimpleFunctionDescriptor, KotlinFunctionDescriptor(impl) {

  override fun impl(): org.jetbrains.kotlin.descriptors.FunctionDescriptor = impl

  override val kind: CallableMemberDescriptor.Kind
    get() =
      when (impl().kind) {
        Kind.DECLARATION -> CallableMemberDescriptor.Kind.DECLARATION
        Kind.FAKE_OVERRIDE -> CallableMemberDescriptor.Kind.FAKE_OVERRIDE
        Kind.DELEGATION -> CallableMemberDescriptor.Kind.DELEGATION
        Kind.SYNTHESIZED -> CallableMemberDescriptor.Kind.SYNTHESIZED
      }
  override val isOperator: Boolean
    get() = impl().isOperator
  override val isInfix: Boolean
    get() = impl().isInfix
  override val isInline: Boolean
    get() = impl().isInline
  override val isTailrec: Boolean
    get() = impl().isTailrec
  override val isSuspend: Boolean
    get() = impl().isSuspend
}
