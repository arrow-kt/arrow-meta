package arrow.refinement.chars

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [UpperCaseChar] constrains [Char] to a uppercase char
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.chars.UpperCaseChar
 *
 * UpperCaseChar.orNull('A')
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.orNull('a')
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.orNull('%')
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * UpperCaseChar.constraints('A')
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.constraints('a')
 * ```
 *
 *  ```kotlin:ank
 * UpperCaseChar.isValid('A')
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.isValid('a')
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.isValid('%')
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * UpperCaseChar.fold('A', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.fold('a', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * UpperCaseChar.fold('%', { "failed: $it" }, { "success: $it" })
 * ```
 *
 *  # Unsafe require
 *
 * ```kotlin:ank
 * UpperCaseChar.require('A')
 * ```
 *
 * ```kotlin:ank
 * try { UpperCaseChar.require('a') } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class UpperCaseChar private constructor(val value: Char) {
  companion object : Refined<Char, UpperCaseChar>(::UpperCaseChar, {
    ensure(it.isUpperCase() to "Expected $it to be lower case")
  })
}