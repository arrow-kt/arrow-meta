package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * JVM only
 *
 * [ValidBigInt] constrains [String] to be a valid [java.math.BigInteger]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.ValidBigInt
 *
 * ValidBigInt.orNull("10000000003334534553445345")
 * ```
 *
 * ```kotlin:ank
 * ValidBigInt.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * ValidBigInt.constraints("10000000003334534553445345")
 * ```
 *
 * ```kotlin:ank
 * ValidBigInt.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * ValidBigInt.isValid("10000000003334534553445345")
 * ```
 *
 * ```kotlin:ank
 * ValidBigInt.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * ValidBigInt.fold("10000000003334534553445345", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * ValidBigInt.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * ValidBigInt.require("10000000003334534553445345")
 * ```
 *
 * ```kotlin:ank
 * try { ValidBigInt.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class ValidBigInt private constructor(val value: String) {
  companion object : Refined<String, ValidBigInt>(::ValidBigInt, {
    ensure((it.toBigIntegerOrNull() != null) to ("Expected $it to be a valid BigInt"))
  })
}