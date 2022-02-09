package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind

abstract class KotlinFunctionDescriptor(
  open val impl: org.jetbrains.kotlin.descriptors.FunctionDescriptor
) : FunctionDescriptor, KotlinCallableMemberDescriptor {

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

class KotlinDefaultFunctionDescriptor(impl: org.jetbrains.kotlin.descriptors.FunctionDescriptor) :
  KotlinFunctionDescriptor(impl)
