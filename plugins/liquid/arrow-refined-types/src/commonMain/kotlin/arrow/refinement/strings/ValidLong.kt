package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [ValidLong] constrains [String] to be a valid [Long]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.ValidLong
 *
 * ValidLong.orNull("10")
 * ```
 *
 * ```kotlin:ank
 * ValidLong.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * ValidLong.constraints("10")
 * ```
 *
 * ```kotlin:ank
 * ValidLong.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * ValidLong.isValid("10")
 * ```
 *
 * ```kotlin:ank
 * ValidLong.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * ValidLong.fold("10", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * ValidLong.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * ValidLong.require("10")
 * ```
 *
 * ```kotlin:ank
 * try { ValidLong.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class ValidLong private constructor(val value: String) {
  companion object : Refined<String, ValidLong>(::ValidLong, {
    ensure((it.toLongOrNull() != null) to ("Expected $it to be a valid Long"))
  })
}
