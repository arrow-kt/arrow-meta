package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * JVM only
 *
 * [ValidBigDecimal] constrains [String] to be a valid [java.math.BigDecimal]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.ValidBigDecimal
 *
 * ValidBigDecimal.orNull("10000.0000033")
 * ```
 *
 * ```kotlin:ank
 * ValidBigDecimal.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * ValidBigDecimal.constraints("10000.0000033")
 * ```
 *
 * ```kotlin:ank
 * ValidBigDecimal.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * ValidBigDecimal.isValid("10000.0000033")
 * ```
 *
 * ```kotlin:ank
 * ValidBigDecimal.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * ValidBigDecimal.fold("10000.0000033", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * ValidBigDecimal.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * ValidBigDecimal.require("10000.0000033")
 * ```
 *
 * ```kotlin:ank
 * try { ValidBigDecimal.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class ValidBigDecimal private constructor(val value: String) {
  companion object : Refined<String, ValidBigDecimal>(::ValidBigDecimal, {
    ensure((it.toBigDecimalOrNull() != null) to ("Expected $it to be a valid BigDecimal"))
  })
}
