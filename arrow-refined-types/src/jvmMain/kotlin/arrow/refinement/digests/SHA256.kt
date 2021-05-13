package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size

@JvmInline
value
/**
 * [SHA256] constrains [String] to be a valid hexadecimal [String] of length 64
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.digests.SHA256
 *
 * SHA256.orNull("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
 * ```
 *
 * ```kotlin:ank
 * SHA256.orNull("not-sha256")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * SHA256.constraints("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
 * ```
 *
 * ```kotlin:ank
 * SHA256.constraints("not-sha256")
 * ```
 *
 *  ```kotlin:ank
 * SHA256.isValid("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
 * ```
 *
 * ```kotlin:ank
 * SHA256.isValid("not-sha256")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * SHA256.fold("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * SHA256.fold("not-sha256", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * SHA256.require("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
 * ```
 *
 * ```kotlin:ank
 * try { SHA256.require("not-sha256") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class SHA256 private constructor(val value: String) {
  companion object : Refined<String, SHA256>(::SHA256, HexString and Size.N(64u))
}