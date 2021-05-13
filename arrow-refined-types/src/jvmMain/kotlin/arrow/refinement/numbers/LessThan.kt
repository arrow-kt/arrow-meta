package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure
import arrow.refinement.numbers.LessThan.N

@JvmInline
value
/**
 * [LessThan] constrains [Int] to be < [N]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.LessThan
 *
 * LessThan.N(1u).orNull(2)
 * ```
 *
 * ```kotlin:ank
 * LessThan.N(1u).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * LessThan.N(1u).constraints(2)
 * ```
 *
 * ```kotlin:ank
 * LessThan.N(1u).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * LessThan.N(1u).isValid(2)
 * ```
 *
 * ```kotlin:ank
 * LessThan.N(1u).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * LessThan.N(1u).fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * LessThan.N(1u).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * LessThan.N(1u).require(0)
 * ```
 *
 * ```kotlin:ank
 * try { LessThan.N(1u).require(1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class LessThan private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be less than $n" }) :
    Refined<Int, LessThan>(::LessThan, {
      ensure((it < n.toInt()) to msg(it))
    })
}