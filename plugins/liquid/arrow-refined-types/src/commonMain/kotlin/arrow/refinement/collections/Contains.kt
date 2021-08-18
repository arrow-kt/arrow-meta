package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * Contains constrains a collection to include an element
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.Contains
 *
 * Contains.Element(1).orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Contains.Element(1).orNull(listOf(2, 3))
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Contains.Element(1).constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Contains.Element(1).constraints(listOf(2, 3))
 * ```
 *
 *  ```kotlin:ank
 * Contains.Element(1).isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * Contains.Element(1).isValid(listOf(2, 3))
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Contains.Element(1).fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Contains.Element(1).fold(listOf(2, 3), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Contains.Element(1).require(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * try { Contains.Element(1).require(listOf(2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Contains<A> private constructor(val value: Iterable<A>) {
  class Element<A>(Element: A, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<A>, Contains<A>>(::Contains, {
    ensure((Element in it) to (msg(it) ?: "Expected $it to contain $Element"))
  })
}
