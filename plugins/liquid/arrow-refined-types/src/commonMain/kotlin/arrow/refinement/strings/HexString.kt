package arrow.refinement.strings

import arrow.refinement.Refined
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [HexString] constrains [String] to be a valid hexadecimal string
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.HexString
 *
 * HexString.orNull("fd00000aa866")
 * ```
 *
 * ```kotlin:ank
 * HexString.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * HexString.constraints("fd00000aa866")
 * ```
 *
 * ```kotlin:ank
 * HexString.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * HexString.isValid("fd00000aa866")
 * ```
 *
 * ```kotlin:ank
 * HexString.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * HexString.fold("fd00000aa866", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * HexString.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * HexString.require("fd00000aa866")
 * ```
 *
 * ```kotlin:ank
 * try { HexString.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class HexString private constructor(val value: String) {
  companion object : Refined<String, HexString>(::HexString,
    MatchesRegex.Regex("^(([0-9a-f]+)|([0-9A-F]+))$".toRegex()) {
      "Expected $it to be a valid hexadecimal string"
    })
}
