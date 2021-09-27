package arrow.meta.plugins.liquid.types

import arrow.meta.phases.resolve.unwrappedNotNullableType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isByte
import org.jetbrains.kotlin.types.typeUtil.isChar
import org.jetbrains.kotlin.types.typeUtil.isDouble
import org.jetbrains.kotlin.types.typeUtil.isFloat
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isLong
import org.jetbrains.kotlin.types.typeUtil.isShort
import org.jetbrains.kotlin.types.typeUtil.isUnsignedNumberType

enum class PrimitiveType {
  BOOLEAN, INTEGRAL, RATIONAL, CHAR
}

fun KotlinType.unwrapIfNullable(): KotlinType =
  if (isMarkedNullable || isNullable())
    unwrappedNotNullableType
  else
    this

fun KotlinType.primitiveType(): PrimitiveType? =
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
