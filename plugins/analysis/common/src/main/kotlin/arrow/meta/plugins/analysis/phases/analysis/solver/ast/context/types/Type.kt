package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor

interface Type : Comparable<Type> {
  val descriptor: ClassDescriptor?
  val unwrappedNotNullableType: Type
  val isMarkedNullable: Boolean
  val arguments: List<TypeProjection>

  fun isBoolean(): Boolean
  fun isNullable(): Boolean
  fun isSubtypeOf(other: Type): Boolean
  fun isEqualTo(other: Type): Boolean
  fun isInt(): Boolean
  fun isLong(): Boolean
  fun isFloat(): Boolean
  fun isDouble(): Boolean
  fun isTypeParameter(): Boolean
  fun isAnyOrNullableAny(): Boolean
  fun isByte(): Boolean
  fun isShort(): Boolean
  fun isUnsignedNumberType(): Boolean
  fun isChar(): Boolean
  fun isString(): Boolean

  override fun compareTo(other: Type): Int =
    when {
      this.isSubtypeOf(other) -> -1
      other.isSubtypeOf(this) -> 1
      else -> 0
    }
}
