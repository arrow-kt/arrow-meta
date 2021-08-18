package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [First] constrains a collection ensuring it first element matches [Element]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.First
 *
 * First.Element(1).orNull(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * First.Element(1).orNull(listOf(2, 3))
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * First.Element(1).constraints(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * First.Element(1).constraints(listOf(2, 3))
 * ```
 *
 *  ```kotlin:ank
 * First.Element(1).isValid(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * First.Element(1).isValid(listOf(2, 3))
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * First.Element(1).fold(listOf(1, 2), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * First.Element(1).fold(listOf(2, 3), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * First.Element(1).require(listOf(1, 2))
 * ```
 *
 * ```kotlin:ank
 * try { First.Element(1).require(listOf(2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class First<A> private constructor(val value: Iterable<A>) {
  class Element<A>(value: A, msg: (Iterable<*>) -> String? = { null }) : Refined<Iterable<A>, First<A>>(::First, {
    ensure((it.firstOrNull() == value) to (msg(it) ?: "Expected first: [${it.firstOrNull()}] to be $value"))
  })
}
