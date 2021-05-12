package arrow.refinement.chars

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [WhiteSpaceChar] constrains [Char] to a white space char
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.chars.WhiteSpaceChar
 *
 * WhiteSpaceChar.orNull(' ')
 * ```
 *
 * ```kotlin:ank
 * WhiteSpaceChar.orNull('a')
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * WhiteSpaceChar.constraints('A')
 * ```
 *
 * ```kotlin:ank
 * WhiteSpaceChar.constraints(' ')
 * ```
 *
 * ```kotlin:ank
 * WhiteSpaceChar.isValid(' ')
 * ```
 *
 * ```kotlin:ank
 * WhiteSpaceChar.isValid('A')
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * WhiteSpaceChar.fold('A', { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * WhiteSpaceChar.fold(' ', { "failed: $it" }, { "success: $it" })
 * ```
 *
 *  # Unsafe require
 *
 * ```kotlin:ank
 * WhiteSpaceChar.require(' ')
 * ```
 *
 * ```kotlin:ank
 * try { WhiteSpaceChar.require('A') } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class WhiteSpaceChar private constructor(val value: Char) {
  companion object : Refined<Char, WhiteSpaceChar>(::WhiteSpaceChar, {
    ensure(it.isWhitespace() to "Expected $it to be white space")
  })
}
