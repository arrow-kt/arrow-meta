package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [EndsWith] constrains [String] to end with [Value]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.EndsWith
 *
 * EndsWith.Value("world").orNull("hello world")
 * ```
 *
 * ```kotlin:ank
 * EndsWith.Value("world").orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * EndsWith.Value("world").constraints("hello world")
 * ```
 *
 * ```kotlin:ank
 * EndsWith.Value("world").constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * EndsWith.Value("world").isValid("hello world")
 * ```
 *
 * ```kotlin:ank
 * EndsWith.Value("world").isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * EndsWith.Value("world").fold("hello world", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * EndsWith.Value("world").fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * EndsWith.Value("world").require("hello world")
 * ```
 *
 * ```kotlin:ank
 * try { EndsWith.Value("world").require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class EndsWith private constructor(val value: String) {
  class Value(value: String, msg: (String) -> String? = { null }) : Refined<String, EndsWith>(::EndsWith, {
    ensure((it.endsWith(value, ignoreCase = false)) to (msg(it) ?: "Expected $it to end with $value"))
  })
}
