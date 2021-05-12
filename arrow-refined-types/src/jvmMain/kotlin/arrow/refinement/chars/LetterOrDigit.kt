package arrow.refinement.chars

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [LetterOrDigit] constrains [Char] to a decimal digit number or a letter
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.chars.LetterOrDigit
 *
 * LetterOrDigit.orNull('1')
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.orNull('a')
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.orNull('%')
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * LetterOrDigit.constraints('1')
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.constraints('a')
 * ```
 *
 *  ```kotlin:ank
 * LetterOrDigit.isValid('1')
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.isValid('a')
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.isValid('%')
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * LetterOrDigit.fold('1', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.fold('a', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * LetterOrDigit.fold('%', { "failed: $it" }, { "success: $it" })
 * ```
 *
 *  # Unsafe require
 *
 * ```kotlin:ank
 * LetterOrDigit.require('1')
 * ```
 *
 *  ```kotlin:ank
 * LetterOrDigit.require('a')
 * ```
 *
 * ```kotlin:ank
 * try { LetterOrDigit.require('%') } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class LetterOrDigit private constructor(val value: Char) {
  companion object : Refined<Char, LetterOrDigit>(::LetterOrDigit, {
    ensure(it.isLetterOrDigit() to "Expected $it to be a letter or digit")
  })
}