package arrow.meta.plugins.analysis.types

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

enum class PrimitiveType {
  BOOLEAN,
  INTEGRAL,
  RATIONAL,
  CHAR,
  STRING
}

fun Type.unwrapIfNullable(): Type =
  if (isMarkedNullable || isNullable()) unwrappedNotNullableType else this

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
    isString() -> PrimitiveType.STRING
    else ->
      when (this.descriptor?.fqNameSafe) {
        FqName("java.math.BigInteger") -> PrimitiveType.INTEGRAL
        FqName("java.math.BigDecimal") -> PrimitiveType.RATIONAL
        else -> null
      }
  }
