package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [MD5] constrains [String] to be a valid hexadecimal [String] of length 32
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.digests.MD5
 *
 * MD5.orNull("098f6bcd4621d373cade4e832627b4f6")
 * ```
 *
 * ```kotlin:ank
 * MD5.orNull("not-md5")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * MD5.constraints("098f6bcd4621d373cade4e832627b4f6")
 * ```
 *
 * ```kotlin:ank
 * MD5.constraints("not-md5")
 * ```
 *
 *  ```kotlin:ank
 * MD5.isValid("098f6bcd4621d373cade4e832627b4f6")
 * ```
 *
 * ```kotlin:ank
 * MD5.isValid("not-md5")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * MD5.fold("098f6bcd4621d373cade4e832627b4f6", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * MD5.fold("not-md5", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * MD5.require("098f6bcd4621d373cade4e832627b4f6")
 * ```
 *
 * ```kotlin:ank
 * try { MD5.require("not-md5") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class MD5 private constructor(val value: String) {
  companion object : Refined<String, MD5>(::MD5, HexString and Size.N(32u))
}