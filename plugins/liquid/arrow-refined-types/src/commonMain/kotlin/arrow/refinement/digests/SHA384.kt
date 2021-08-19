package arrow.refinement.digests

import arrow.refinement.Refined
import arrow.refinement.strings.HexString
import arrow.refinement.strings.Size
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [SHA384] constrains [String] to be a valid hexadecimal [String] of length 96
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.digests.SHA384
 *
 * SHA384.orNull("768412320f7b0aa5812fce428dc4706b3cae50e02a64caa16a782249bfe8efc4b7ef1ccb126255d196047dfedf17a0a9")
 * ```
 *
 * ```kotlin:ank
 * SHA384.orNull("not-sha384")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * SHA384.constraints("768412320f7b0aa5812fce428dc4706b3cae50e02a64caa16a782249bfe8efc4b7ef1ccb126255d196047dfedf17a0a9")
 * ```
 *
 * ```kotlin:ank
 * SHA384.constraints("not-sha384")
 * ```
 *
 *  ```kotlin:ank
 * SHA384.isValid("768412320f7b0aa5812fce428dc4706b3cae50e02a64caa16a782249bfe8efc4b7ef1ccb126255d196047dfedf17a0a9")
 * ```
 *
 * ```kotlin:ank
 * SHA384.isValid("not-sha384")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * SHA384.fold("768412320f7b0aa5812fce428dc4706b3cae50e02a64caa16a782249bfe8efc4b7ef1ccb126255d196047dfedf17a0a9", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * SHA384.fold("not-sha384", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * SHA384.require("768412320f7b0aa5812fce428dc4706b3cae50e02a64caa16a782249bfe8efc4b7ef1ccb126255d196047dfedf17a0a9")
 * ```
 *
 * ```kotlin:ank
 * try { SHA384.require("not-sha384") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class SHA384 private constructor(val value: String) {
  companion object : Refined<String, SHA384>(::SHA384, HexString and Size.N(96u))
}
