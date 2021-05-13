package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value
/**
 * [Count] constrains a collection to be have [N] number of elements
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.Count
 *
 * Count.N(1u).orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Count.N(1u).orNull(listOf(1))
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Count.N(1u).constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Count.N(1u).constraints(listOf(1))
 * ```
 *
 *  ```kotlin:ank
 * Count.N(1u).isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Count.N(1u).isValid(listOf(1))
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Count.N(1u).fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Count.N(1u).fold(listOf(1), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Count.N(1u).require(listOf(1))
 * ```
 *
 * ```kotlin:ank
 * try { Count.N(1u).require(listOf(2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Count private constructor(val value: Iterable<*>) {
  class N(value: UInt, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<*>, Count>(::Count, {
    ensure((it.count() == value.toInt()) to (msg(it) ?: "Expected count to be $value"))
  })
}