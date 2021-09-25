package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind.*

fun interface KotlinFunctionDescriptor : FunctionDescriptor, KotlinCallableMemberDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.FunctionDescriptor

  override val kind: CallableMemberDescriptor.Kind
    get() = when (impl().kind) {
      DECLARATION -> CallableMemberDescriptor.Kind.DECLARATION
      FAKE_OVERRIDE -> CallableMemberDescriptor.Kind.FAKE_OVERRIDE
      DELEGATION -> CallableMemberDescriptor.Kind.DELEGATION
      SYNTHESIZED -> CallableMemberDescriptor.Kind.SYNTHESIZED
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
