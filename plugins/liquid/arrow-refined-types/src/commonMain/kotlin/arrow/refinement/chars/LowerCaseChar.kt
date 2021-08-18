package arrow.refinement.chars

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [LowerCaseChar] constrains [Char] to a lowercase char
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.chars.LowerCaseChar
 *
 * LowerCaseChar.orNull('A')
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.orNull('a')
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.orNull('%')
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * LowerCaseChar.constraints('A')
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.constraints('a')
 * ```
 *
 *  ```kotlin:ank
 * LowerCaseChar.isValid('A')
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.isValid('a')
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.isValid('%')
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * LowerCaseChar.fold('A', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.fold('a', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * LowerCaseChar.fold('%', { "failed: $it" }, { "success: $it" })
 * ```
 *
 *  # Unsafe require
 *
 *  ```kotlin:ank
 * LowerCaseChar.require('a')
 * ```
 *
 * ```kotlin:ank
 * try { LowerCaseChar.require('A') } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class LowerCaseChar private constructor(val value: Char) {
  companion object : Refined<Char, LowerCaseChar>(::LowerCaseChar, {
    ensure(it.isLowerCase() to "Expected $it to be lower case")
  })
}
