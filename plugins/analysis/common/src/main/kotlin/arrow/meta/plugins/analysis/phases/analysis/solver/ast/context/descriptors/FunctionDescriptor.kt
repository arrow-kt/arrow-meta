package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

interface FunctionDescriptor : CallableMemberDescriptor {
  val isOperator: Boolean
  val isInfix: Boolean
  val isInline: Boolean
  val isTailrec: Boolean
  val isSuspend: Boolean
}

/**
 * Removes the indirection from type aliases in a descriptor.
 */
val DeclarationDescriptor.withAliasUnwrapped: DeclarationDescriptor
  get() = when (this) {
    is FunctionDescriptor -> this.withAliasUnwrapped
    else -> this
  }

/**
 * Removes the indirection from type aliases in a descriptor.
 */
val FunctionDescriptor.withAliasUnwrapped: FunctionDescriptor
  get() = when (this) {
    is TypeAliasConstructorDescriptor ->
      this.underlyingConstructorDescriptor.withAliasUnwrapped
    else -> this
  }
