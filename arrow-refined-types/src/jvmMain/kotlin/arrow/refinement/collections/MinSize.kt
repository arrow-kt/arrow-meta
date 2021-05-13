package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.collections.MinSize.N
import arrow.refinement.ensure

@JvmInline
value
/**
 * [MinSize] constrains a collection to be have a minimum [N] number of elements
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.MinSize
 *
 * MinSize.N(2u).orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * MinSize.N(2u).orNull(listOf(1))
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * MinSize.N(2u).constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * MinSize.N(2u).constraints(listOf(1))
 * ```
 *
 *  ```kotlin:ank
 * MinSize.N(2u).isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * MinSize.N(2u).isValid(listOf(1))
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * MinSize.N(2u).fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * MinSize.N(2u).fold(listOf(1), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * MinSize.N(1u).require(listOf(1))
 * ```
 *
 * ```kotlin:ank
 * try { MinSize.N(3u).require(listOf(2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class MinSize private constructor(val value: Iterable<*>) {
  class N(value: UInt, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<*>, MinSize>(::MinSize, {
    ensure((it.count() >= value.toInt()) to (msg(it) ?: "Expected min size of $value but found ${it.count()}"))
  })
}