package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Equal
import arrow.refinement.numbers.To.N

@JvmInline
value
/**
 * [To] constrains [Int] to be <= [N]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.To
 *
 * To.N(1u).orNull(2)
 * ```
 *
 * ```kotlin:ank
 * To.N(1u).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * To.N(1u).constraints(2)
 * ```
 *
 * ```kotlin:ank
 * To.N(1u).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * To.N(1u).isValid(2)
 * ```
 *
 * ```kotlin:ank
 * To.N(1u).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * To.N(1u).fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * To.N(1u).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * To.N(1u).require(0)
 * ```
 *
 * ```kotlin:ank
 * try { To.N(1u).require(2) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class To private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be less than or equal to $n" }) :
    Refined<Int, To>(::To, LessThan.N(n) or Equal.Value(n.toInt()))
}