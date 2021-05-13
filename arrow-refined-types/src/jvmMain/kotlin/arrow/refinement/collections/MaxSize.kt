package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.collections.Count.N
import arrow.refinement.ensure

@JvmInline
value
/**
 * [MaxSize] constrains a collection to be have a maximum [N] number of elements
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.MaxSize
 *
 * MaxSize.N(1u).orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * MaxSize.N(1u).orNull(listOf(1))
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * MaxSize.N(1u).constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * MaxSize.N(1u).constraints(listOf(1))
 * ```
 *
 *  ```kotlin:ank
 * MaxSize.N(1u).isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * MaxSize.N(1u).isValid(listOf(1))
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * MaxSize.N(1u).fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * MaxSize.N(1u).fold(listOf(1), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * MaxSize.N(1u).require(listOf(1))
 * ```
 *
 * ```kotlin:ank
 * try { MaxSize.N(1u).require(listOf(2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class MaxSize private constructor(val value: Iterable<*>) {
  class N(value: UInt, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<*>, MaxSize>(::MaxSize, {
    ensure((it.count() <= value.toInt()) to (msg(it) ?: "Expected max size of $value but found ${it.count()}"))
  })
}