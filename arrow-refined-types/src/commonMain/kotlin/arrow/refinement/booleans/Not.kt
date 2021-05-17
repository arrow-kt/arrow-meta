package arrow.refinement.booleans

import arrow.refinement.Refined

/**
 * Boolean negation of  a [predicate].
 * !predicate
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.booleans.Equal
 * import arrow.refinement.booleans.Not
 *
 * Not(Equal.Value(1)).orNull(1)
 * ```
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).orNull(0)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).constraints(1)
 * ```
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).constraints(0)
 * ```
 *
 *  ```kotlin:ank
 * Not(Equal.Value(1)).isValid(1)
 * ```
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).isValid(0)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).fold(1, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).fold(0, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * Not(Equal.Value(1)).require(0)
 * ```
 *
 * ```kotlin:ank
 * try { Not(Equal.Value(1)).require(1) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * @see arrow.refinement.Refined.not
 */
class Not<A, B>(private val predicate: Refined<A, B>, defaultMsg: (A) -> String? = { null }) :
  Refined<A, B>(predicate.f, {
    predicate.constraints(it).map { (passed, msg) ->
      !passed to (defaultMsg(it) ?: "negated: $msg")
    }
  })