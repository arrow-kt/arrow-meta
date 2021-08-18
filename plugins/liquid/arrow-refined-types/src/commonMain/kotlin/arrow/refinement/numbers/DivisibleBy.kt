package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * DivisibleBy constrains an [Int] to be divisible by [N]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.DivisibleBy
 *
 * DivisibleBy.N(2u).orNull(2)
 * ```
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).orNull(1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).constraints(2)
 * ```
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).constraints(1)
 * ```
 *
 *  ```kotlin:ank
 * DivisibleBy.N(2u).isValid(2)
 * ```
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).isValid(1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * DivisibleBy.N(2u).require(2)
 * ```
 *
 * ```kotlin:ank
 * try { DivisibleBy.N(2u).require(1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class DivisibleBy private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be divisible by $n" }) :
    Refined<Int, DivisibleBy>(::DivisibleBy, {
      ensure((it % n.toInt() == 0) to msg(it))
    })
}