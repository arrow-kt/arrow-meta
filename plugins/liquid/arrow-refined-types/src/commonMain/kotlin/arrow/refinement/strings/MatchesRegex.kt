package arrow.refinement.strings

import arrow.refinement.Refined
import arrow.refinement.ensure
import arrow.refinement.strings.EndsWith.Value
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [MatchesRegex] constrains [String] to match a given regular expression
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.strings.MatchesRegex
 *
 * MatchesRegex.Regex("hello world".toRegex()).orNull("hello world")
 * ```
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).orNull("abc")
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).constraints("hello world")
 * ```
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).constraints("abc")
 * ```
 *
 *  ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).isValid("hello world")
 * ```
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).isValid("abc")
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).fold("hello world", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).fold("abc", { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * MatchesRegex.Regex("hello world".toRegex()).require("hello world")
 * ```
 *
 * ```kotlin:ank
 * try { MatchesRegex.Regex("hello world".toRegex()).require("abc") } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class MatchesRegex private constructor(val value: String) {
  class Regex(value: kotlin.text.Regex, msg: (String) -> String? = { null }) : Refined<String, MatchesRegex>(::MatchesRegex, {
    ensure((it.matches(value)) to (msg(it) ?: "Expected $it to match $value"))
  })
}