package arrow.refinement.chars

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [Letter] constrains [Char] to a letter
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.chars.Letter
 *
 * Letter.orNull('1')
 * ```
 *
 * ```kotlin:ank
 * Letter.orNull('a')
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Letter.constraints('1')
 * ```
 *
 * ```kotlin:ank
 * Letter.constraints('a')
 * ```
 *
 *  ```kotlin:ank
 * Letter.isValid('1')
 * ```
 *
 * ```kotlin:ank
 * Letter.isValid('a')
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Letter.fold('1', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Letter.fold('a', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { Letter.require('1') } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * Letter.require('a')
 * ```
 *
 */
class Letter private constructor(val value: Char) {
  companion object : Refined<Char, Letter>(::Letter, {
    ensure(it.isLetter() to "Expected $it to be a letter")
  })
}