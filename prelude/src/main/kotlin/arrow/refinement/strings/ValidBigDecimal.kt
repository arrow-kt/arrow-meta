package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class ValidBigDecimal private constructor(val value: String) {
  companion object : Refined<String, ValidBigDecimal>(::ValidBigDecimal, {
    ensure((it.toBigDecimalOrNull() != null) to ("Expected $it to be a valid BigDecimal"))
  })
}