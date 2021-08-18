package arrow.refinement.numbers

import arrow.refinement.Refined
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [FromTo] constrains [Int] to be in range of [From] to [To]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.FromTo
 *
 * FromTo.N(1u, 5u).orNull(2)
 * ```
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).constraints(2)
 * ```
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * FromTo.N(1u, 5u).isValid(2)
 * ```
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * FromTo.N(1u, 5u).require(2)
 * ```
 *
 * ```kotlin:ank
 * try { FromTo.N(1u, 5u).require(0) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class FromTo private constructor(val value: Int) {
  class N(from: UInt, to: UInt, msg: (Int) -> String = { "$it should be in range $from..$to" }) :
    Refined<Int, FromTo>(::FromTo, From.N(from, msg) and To.N(to, msg))
}