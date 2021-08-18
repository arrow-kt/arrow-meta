package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [ValidInt] constrains [String] to be a valid [Int]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.ValidInt
 *
 * ValidInt.orNull("10")
 * ```
 *
 * ```kotlin:ank
 * ValidInt.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * ValidInt.constraints("10")
 * ```
 *
 * ```kotlin:ank
 * ValidInt.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * ValidInt.isValid("10")
 * ```
 *
 * ```kotlin:ank
 * ValidInt.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * ValidInt.fold("10", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * ValidInt.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * ValidInt.require("10")
 * ```
 *
 * ```kotlin:ank
 * try { ValidInt.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class ValidInt private constructor(val value: String) {
  companion object : Refined<String, ValidInt>(::ValidInt, {
    ensure((it.toIntOrNull() != null) to ("Expected $it to be a valid Int"))
  })
}