package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [GreaterThan] constrains [Int] to be > [N]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.GreaterThan
 *
 * GreaterThan.N(1u).orNull(2)
 * ```
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).constraints(2)
 * ```
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * GreaterThan.N(1u).isValid(2)
 * ```
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * GreaterThan.N(1u).require(2)
 * ```
 *
 * ```kotlin:ank
 * try { GreaterThan.N(1u).require(0) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class GreaterThan private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be greater than $n" }) :
    Refined<Int, GreaterThan>(::GreaterThan, {
      ensure((it > n.toInt()) to msg(it))
    })
}