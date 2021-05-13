package arrow.refinement.collections

import arrow.refinement.Refined

@JvmInline
value
/**
 * [Empty] constrains a collection to be empty
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.Empty
 *
 * Empty.orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Empty.orNull(emptyList<Int>())
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Empty.constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Empty.constraints(emptyList<Int>())
 * ```
 *
 *  ```kotlin:ank
 * Empty.isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Empty.isValid(emptyList<Int>())
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Empty.fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Empty.fold(emptyList<Int>(), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Empty.require(emptyList<Int>())
 * ```
 *
 * ```kotlin:ank
 * try { Empty.require(listOf(2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Empty private constructor(val value: Iterable<*>) {
  companion object : Refined<Iterable<*>, Empty>(::Empty, Count.N(0u) {
    "Expected empty iterable but found $it"
  })
}