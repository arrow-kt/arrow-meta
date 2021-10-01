package arrow.meta.plugins.analysis.types

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Parse integer literal according to Kotlin's grammar
 * https://kotlinlang.org/docs/reference/grammar.html#literalConstant
 */
fun String.asIntegerLiteral(): BigInteger? =
  replace("_", "")
    .trimEnd('u', 'U', 'l', 'L')
    .run {
      when {
        startsWith("0x", ignoreCase = true) ->
          drop(2).toBigIntegerOrNull(16)
        startsWith("0b", ignoreCase = true) ->
          drop(2).toBigIntegerOrNull(2)
        else -> toBigIntegerOrNull()
      }
    }

/**
 * Parse floating literal according to Kotlin's grammar
 * https://kotlinlang.org/docs/reference/grammar.html#RealLiteral
 */
fun String.asFloatingLiteral(): BigDecimal? =
  replace("_", "")
    .trimEnd('f', 'F')
    .toBigDecimalOrNull()
