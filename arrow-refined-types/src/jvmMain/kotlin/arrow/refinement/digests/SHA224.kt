package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size

@JvmInline
value
/**
 * [SHA224] constrains [String] to be a valid hexadecimal [String] of length 56
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.digests.SHA224
 *
 * SHA224.orNull("90a3ed9e32b2aaf4c61c410eb925426119e1a9dc53d4286ade99a809")
 * ```
 *
 * ```kotlin:ank
 * SHA224.orNull("not-sha224")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * SHA224.constraints("90a3ed9e32b2aaf4c61c410eb925426119e1a9dc53d4286ade99a809")
 * ```
 *
 * ```kotlin:ank
 * SHA224.constraints("not-sha224")
 * ```
 *
 *  ```kotlin:ank
 * SHA224.isValid("90a3ed9e32b2aaf4c61c410eb925426119e1a9dc53d4286ade99a809")
 * ```
 *
 * ```kotlin:ank
 * SHA224.isValid("not-sha224")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * SHA224.fold("90a3ed9e32b2aaf4c61c410eb925426119e1a9dc53d4286ade99a809", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * SHA224.fold("not-sha224", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * SHA224.require("90a3ed9e32b2aaf4c61c410eb925426119e1a9dc53d4286ade99a809")
 * ```
 *
 * ```kotlin:ank
 * try { SHA224.require("not-sha224") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class SHA224 private constructor(val value: String) {
  companion object : Refined<String, SHA224>(::SHA224, HexString and Size.N(56u))
}