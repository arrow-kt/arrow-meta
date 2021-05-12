package arrow.refinement.chars

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [Digit] constrains [Char] to a decimal digit number
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.chars.Digit
 *
 * Digit.orNull('1')
 * ```
 *
 * ```kotlin:ank
 * Digit.orNull('a')
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Digit.constraints('1')
 * ```
 *
 * ```kotlin:ank
 * Digit.constraints('a')
 * ```
 *
 *  ```kotlin:ank
 * Digit.isValid('1')
 * ```
 *
 * ```kotlin:ank
 * Digit.isValid('a')
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Digit.fold('1', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Digit.fold('a', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Digit.fold('a', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Digit.require('1')
 * ```
 *
 * ```kotlin:ank
 * try { Digit.require('a') } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Digit private constructor(val value: Char) {

  companion object : Refined<Char, Digit>(::Digit, {
    ensure(it.isDigit() to "Expected $it to be a digit")
  })
}