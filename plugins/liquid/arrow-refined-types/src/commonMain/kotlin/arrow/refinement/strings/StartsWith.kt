package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import arrow.refinement.strings.StartsWith.Value
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [StartsWith] constrains [String] to start with [Value]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.StartsWith
 *
 * StartsWith.Value("hello").orNull("hello world")
 * ```
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").constraints("hello world")
 * ```
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * StartsWith.Value("hello").isValid("hello world")
 * ```
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").fold("hello world", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * StartsWith.Value("hello").require("hello world")
 * ```
 *
 * ```kotlin:ank
 * try { StartsWith.Value("hello").require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class StartsWith private constructor(val value: String) {
  class Value(value: String, msg: (String) -> String? = { null }) : Refined<String, StartsWith>(::StartsWith, {
    ensure((it.startsWith(value, ignoreCase = false)) to (msg(it) ?: "Expected $it to start with $value"))
  })
}