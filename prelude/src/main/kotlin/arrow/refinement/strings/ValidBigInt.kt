package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class ValidBigInt private constructor(val value: String) {
  companion object : Refined<String, ValidBigInt>(::ValidBigInt, {
    ensure((it.toBigDecimalOrNull() != null) to ("Expected $it to be a valid BigInt"))
  })
}