package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * JVM only
 *
 * [UUID] constrains [String] to be a valid [java.util.UUID]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.UUID
 *
 * UUID.orNull("123e4567-e89b-12d3-a456-556642440000")
 * ```
 *
 * ```kotlin:ank
 * UUID.orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * UUID.constraints("123e4567-e89b-12d3-a456-556642440000")
 * ```
 *
 * ```kotlin:ank
 * UUID.constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * UUID.isValid("123e4567-e89b-12d3-a456-556642440000")
 * ```
 *
 * ```kotlin:ank
 * UUID.isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * UUID.fold("123e4567-e89b-12d3-a456-556642440000", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * UUID.fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * UUID.require("123e4567-e89b-12d3-a456-556642440000")
 * ```
 *
 * ```kotlin:ank
 * try { UUID.require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class UUID private constructor(val value: String) {
  companion object : Refined<String, UUID>(::UUID, {
    ensure(
      (try {
        java.util.UUID.fromString(it)
        true
      } catch (e: IllegalArgumentException) {
        false
      }) to ("Expected $it to be a valid UUID")
    )
  })
}