package arrow.refinement.collections

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [Tail] constrains a collection ensuring its tail matches [Elements]
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.collections.Tail
 *
 * Tail.Elements(listOf(2, 3)).orNull(listOf(1, 2, 3))
 * ```
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(3, 4)).orNull(listOf(1, 2, 3))
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(2, 3)).constraints(listOf(1, 2, 3))
 * ```
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(3, 4)).constraints(listOf(1, 2, 3))
 * ```
 *
 *  ```kotlin:ank
 * Tail.Elements(listOf(2, 3)).isValid(listOf(1, 2, 3))
 * ```
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(3, 4)).isValid(listOf(1, 2, 3))
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(2, 3)).fold(listOf(1, 2, 3), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(3, 4)).fold(listOf(1, 2, 3), { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Tail.Elements(listOf(2, 3)).require(listOf(1, 2, 3))
 * ```
 *
 * ```kotlin:ank
 * try { Tail.Elements(listOf(3, 4)).require(listOf(1, 2, 3)) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 */
class Tail<A> private constructor(val value: Iterable<A>) {
  class Elements<A>(value: Iterable<A>, msg: (Iterable<*>) -> String? = { null }) :
    Refined<Iterable<A>, Tail<A>>(::Tail, {
      ensure((it.drop(1) == value) to (msg(it) ?: "Expected tail: [${it.drop(1)}] to be $value"))
    })
}
