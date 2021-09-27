package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.liquid.types.PrimitiveType

interface Type {
  val descriptor: ClassDescriptor?
  val unwrappedNotNullableType: Type
  val isMarkedNullable: Boolean

  fun isBoolean(): Boolean
  fun isNullable(): Boolean
  fun isSubtypeOf(other: Type): Boolean
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
}
