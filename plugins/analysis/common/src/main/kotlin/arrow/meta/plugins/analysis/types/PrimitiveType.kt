package arrow.meta.plugins.analysis.types

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type

enum class PrimitiveType {
  BOOLEAN, INTEGRAL, RATIONAL, CHAR
}

fun Type.unwrapIfNullable(): Type =
  if (isMarkedNullable || isNullable())
    unwrappedNotNullableType
  else
    this

fun Type.primitiveType(): PrimitiveType? =
  when {
    isBoolean() -> PrimitiveType.BOOLEAN
    isByte() -> PrimitiveType.INTEGRAL
    isShort() -> PrimitiveType.INTEGRAL
    isInt() -> PrimitiveType.INTEGRAL
    isLong() -> PrimitiveType.INTEGRAL
    isUnsignedNumberType() -> PrimitiveType.INTEGRAL
    isDouble() -> PrimitiveType.RATIONAL
    isFloat() -> PrimitiveType.RATIONAL
    isChar() -> PrimitiveType.CHAR
    else -> null
  }
