package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [Size] constrains a string to be have [N] length
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.Size
 *
 * Size.N(1u).orNull("a")
 * ```
 *
 * ```kotlin:ank
 * Size.N(1u).orNull("ab")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Size.N(1u).constraints("a")
 * ```
 *
 * ```kotlin:ank
 * Size.N(1u).constraints("ab")
 * ```
 *
 *  ```kotlin:ank
 * Size.N(1u).isValid("a")
 * ```
 *
 * ```kotlin:ank
 * Size.N(1u).isValid("ab")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Size.N(1u).fold("a", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Size.N(1u).fold("ab", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Size.N(1u).require("a")
 * ```
 *
 * ```kotlin:ank
 * try { Size.N(1u).require("ab") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Size private constructor(val value: String) {
  class N(value: UInt, msg: (String) -> String? = { null }) : Refined<String, Size>(::Size, {
    ensure((it.length == value.toInt()) to (msg(it) ?: "Expected $it to have length $value"))
  })
}