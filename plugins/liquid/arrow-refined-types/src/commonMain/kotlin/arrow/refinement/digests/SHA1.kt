package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [SHA1] constrains [String] to be a valid hexadecimal [String] of length 40
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.digests.SHA1
 *
 * SHA1.orNull("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3")
 * ```
 *
 * ```kotlin:ank
 * SHA1.orNull("not-sha1")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * SHA1.constraints("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3")
 * ```
 *
 * ```kotlin:ank
 * SHA1.constraints("not-sha1")
 * ```
 *
 *  ```kotlin:ank
 * SHA1.isValid("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3")
 * ```
 *
 * ```kotlin:ank
 * SHA1.isValid("not-sha1")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * SHA1.fold("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * SHA1.fold("not-sha1", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * SHA1.require("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3")
 * ```
 *
 * ```kotlin:ank
 * try { SHA1.require("not-sha1") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class SHA1 private constructor(val value: String) {
  companion object : Refined<String, SHA1>(::SHA1, HexString and Size.N(40u))
}
