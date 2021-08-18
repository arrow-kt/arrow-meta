package arrow.refinement.numbers

import arrow.refinement.Refined
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * Even constrains an [Int] to be [DivisibleBy] by 2
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.Even
 *
 * Even.orNull(2)
 * ```
 *
 * ```kotlin:ank
 * Even.orNull(1)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Even.constraints(2)
 * ```
 *
 * ```kotlin:ank
 * Even.constraints(1)
 * ```
 *
 *  ```kotlin:ank
 * Even.isValid(2)
 * ```
 *
 * ```kotlin:ank
 * Even.isValid(1)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Even.fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Even.fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Even.require(2)
 * ```
 *
 * ```kotlin:ank
 * try { Even.require(1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Even private constructor(val value: Int) {
  companion object : Refined<Int, Even>(::Even, DivisibleBy.N(2u) {
    "$it should be even"
  })
}