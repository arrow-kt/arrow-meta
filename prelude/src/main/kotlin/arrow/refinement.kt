package arrow

import arrow.TypeProof.Extension
import arrow.TypeProof.Refinement

/**
 * val y: PositiveInt = -3 // not ok
 * val x: PositiveInt = 1 // ok
 * val impossible: PositiveInt = PositiveInt(-5) // fails at compile time
 * val result: PositiveInt = y.value?.let(Int::inc)
 */
inline class PositiveInt(val value: Int?)

@Proof(Refinement)
@Suppress("NOTHING_TO_INLINE")
inline fun Int.toPositiveInt(): PositiveInt =
  if (this >= 0) PositiveInt(this) else PositiveInt(null)

@Proof(Refinement)
@Suppress("NOTHING_TO_INLINE")
inline fun Int?.positive(): PositiveInt =
  this?.toPositiveInt() ?: PositiveInt(this)

@Proof(Extension)
fun PositiveInt.toInt(): Int? =
  value

inline class IntString(val value: String?)

@Proof(Refinement)
fun String.safeInt(): IntString =
  if (this.matches(Regex("-?[0-9]+"))) IntString(this) else IntString(null)

@Proof(Refinement)
fun String?.int(): IntString =
  this?.safeInt() ?: IntString(this)

@Proof(Extension)
fun IntString.string(): String? =
  value

/**
 * ```kotlin
 * val element: IntString = "44"
 * val eleven: Int? = element.value?.div(4)
 * ```
 */
@Proof(Extension)
fun IntString.int(): Int? =
  value?.toIntOrNull()