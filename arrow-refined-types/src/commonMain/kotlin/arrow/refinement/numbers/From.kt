package arrow.refinement.numbers

import arrow.refinement.Refined
import arrow.refinement.booleans.Equal
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [From] constrains [Int] to be >= [N]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.numbers.From
 *
 * From.N(1u).orNull(2)
 * ```
 *
 * ```kotlin:ank
 * From.N(1u).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * From.N(1u).constraints(2)
 * ```
 *
 * ```kotlin:ank
 * From.N(1u).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * From.N(1u).isValid(2)
 * ```
 *
 * ```kotlin:ank
 * From.N(1u).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * From.N(1u).fold(2, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * From.N(1u).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * From.N(1u).require(2)
 * ```
 *
 * ```kotlin:ank
 * try { From.N(1u).require(0) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class From private constructor(val value: Int) {
  class N(n: UInt, msg: (Int) -> String = { "$it should be greater than or equal to $n" }) :
    Refined<Int, From>(::From, GreaterThan.N(n, msg) or Equal.Value(n.toInt(), msg))
}