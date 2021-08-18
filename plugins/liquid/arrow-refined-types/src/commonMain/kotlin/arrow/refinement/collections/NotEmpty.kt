package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.booleans.Not
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [NotEmpty] constrains a collection to be not empty
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.NotEmpty
 *
 * NotEmpty.orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * NotEmpty.orNull(emptyList<Int>())
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * NotEmpty.constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * NotEmpty.constraints(emptyList<Int>())
 * ```
 *
 *  ```kotlin:ank
 * NotEmpty.isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * NotEmpty.isValid(emptyList<Int>())
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * NotEmpty.fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * NotEmpty.fold(emptyList<Int>(), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * NotEmpty.require(listOf(2, 3))
 * ```
 *
 * ```kotlin:ank
 * try { NotEmpty.require(emptyList<Int>()) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class NotEmpty private constructor(val value: Iterable<*>) {
  companion object : Refined<Iterable<*>, NotEmpty>(::NotEmpty, Not(Empty) {
    "Expected non empty iterable but found $it"
  })
}
