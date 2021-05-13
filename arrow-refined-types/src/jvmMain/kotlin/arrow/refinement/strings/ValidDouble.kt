package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [ValidDouble] constrains [String] to be a valid [Double]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.ValidDouble
 *
 * ValidDouble.orNull("10.0")
 * ```
 *
 * ```kotlin:ank
 * ValidDouble.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * ValidDouble.constraints("10.0")
 * ```
 *
 * ```kotlin:ank
 * ValidDouble.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * ValidDouble.isValid("10.0")
 * ```
 *
 * ```kotlin:ank
 * ValidDouble.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * ValidDouble.fold("10.0", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * ValidDouble.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * ValidDouble.require("10.0")
 * ```
 *
 * ```kotlin:ank
 * try { ValidDouble.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class ValidDouble private constructor(val value: String) {
  companion object : Refined<String, ValidDouble>(::ValidDouble, {
    ensure((it.toDoubleOrNull() != null) to ("Expected $it to be a valid Double"))
  })
}