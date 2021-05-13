package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size

@JvmInline
value
/**
 * [SHA512] constrains [String] to be a valid hexadecimal [String] of length 128
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.digests.SHA512
 *
 * SHA512.orNull("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff")
 * ```
 *
 * ```kotlin:ank
 * SHA512.orNull("not-sha512")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * SHA512.constraints("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff")
 * ```
 *
 * ```kotlin:ank
 * SHA512.constraints("not-sha512")
 * ```
 *
 *  ```kotlin:ank
 * SHA512.isValid("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff")
 * ```
 *
 * ```kotlin:ank
 * SHA512.isValid("not-sha512")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * SHA512.fold("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * SHA512.fold("not-sha512", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * SHA512.require("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff")
 * ```
 *
 * ```kotlin:ank
 * try { SHA512.require("not-sha512") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class SHA512 private constructor(val value: String) {
  companion object : Refined<String, SHA512>(::SHA512, HexString and Size.N(128u))
}
