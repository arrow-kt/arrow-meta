package arrow.refinement.booleans

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * Equal constrains [Any?] to be equal to an initial value
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.booleans.Equal
 *
 * Equal.Value(1).orNull(1)
 * ```
 *
 * ```kotlin:ank
 * Equal.Value(1).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Equal.Value(1).constraints(1)
 * ```
 *
 * ```kotlin:ank
 * Equal.Value(1).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * Equal.Value(1).isValid(1)
 * ```
 *
 * ```kotlin:ank
 * Equal.Value(1).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Equal.Value(1).fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Equal.Value(1).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Equal.Value(1).require(1)
 * ```
 *
 * ```kotlin:ank
 * try { Equal.Value(1).require(0) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Equal private constructor(val value: Any?) {
  class Value<A>(value: A, msg: (A) -> String = { "$it should be == $value" }) :
    Refined<A, Equal>(::Equal, {
      ensure((it == value) to msg(it))
    })
}