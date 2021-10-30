// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

@Laws
object MathLaws {
  @Law
  inline fun Int.absoluteValueLaw(): Int =
    absoluteValue.post({ it >= 0 && (if (this >= 0) it == this else it == -this) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun Long.absoluteValueLaw(): Long =
    absoluteValue.post({ it >= 0L && (if (this >= 0L) it == this else it == -this) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun Float.absoluteValueLaw(): Float =
    absoluteValue.post({ it >= 0.0F && (if (this >= 0.0F) it == this else it == -this) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun Double.absoluteValueLaw(): Double =
    absoluteValue.post({ it >= 0.0 && (if (this >= 0.0) it == this else it == -this) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun absLaw(x: Int): Int =
    abs(x).post({ it >= 0 && (if (x >= 0) it == x else it == -x) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun absLaw(x: Long): Long =
    abs(x).post({ it >= 0L && (if (x >= 0L) it == x else it == -x) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun absLaw(x: Float): Float =
    abs(x).post({ it >= 0.0F && (if (x >= 0.0F) it == x else it == -x) }) {
      "absolute value is non-negative + definition"
    }
  @Law
  inline fun absLaw(x: Double): Double =
    abs(x).post({ it >= 0.0 && (if (x >= 0.0) it == x else it == -x) }) {
      "absolute value is non-negative + definition"
    }

  @Law
  inline fun Int.signLaw(): Int =
    sign.post({
      it >= -1 &&
        it <= 1 &&
        when {
          this == 0 -> it == 0
          this > 0 -> it == 1
          else -> it == -1
        }
    }) { "sign bounds + definition" }
  @Law
  inline fun Long.signLaw(): Int =
    sign.post({
      it >= -1 &&
        it <= 1 &&
        when {
          this == 0L -> it == 0
          this > 0L -> it == 1
          else -> it == -1
        }
    }) { "sign bounds + definition" }
  @Law
  inline fun Double.signLaw(): Double =
    sign.post({
      it >= -1.0 &&
        it <= 1.0 &&
        when {
          this == 0.0 -> it == 0.0
          this > 0.0 -> it == 1.0
          else -> it == -1.0
        }
    }) { "sign bounds + definition" }
  @Law
  inline fun Float.signLaw(): Float =
    sign.post({
      it >= -1.0F &&
        it <= 1.0F &&
        when {
          this == 0.0F -> it == 0.0F
          this > 0.0F -> it == 1.0F
          else -> it == -1.0F
        }
    }) { "sign bounds + definition" }

  @Law
  inline fun signTopLevelLaw(x: Double): Double =
    sign(x).post({
      it >= -1.0 &&
        it <= 1.0 &&
        when {
          x == 0.0 -> it == 0.0
          x > 0.0 -> it == 1.0
          else -> it == -1.0
        }
    }) { "sign bounds + definition" }
  @Law
  inline fun signTopLevelLaw(x: Float): Float =
    sign(x).post({
      it >= -1.0F &&
        it <= 1.0F &&
        when {
          x == 0.0F -> it == 0.0F
          x > 0.0F -> it == 1.0F
          else -> it == -1.0F
        }
    }) { "sign bounds + definition" }

  @Law
  inline fun maxLaw(a: Int, b: Int): Int =
    max(a, b).post({ it >= a && it >= b && (if (a >= b) it == a else it == b) }) {
      "bounds for max + definition"
    }
  @Law
  inline fun maxLaw(a: Long, b: Long): Long =
    max(a, b).post({ it >= a && it >= b && (if (a >= b) it == a else it == b) }) {
      "bounds for max + definition"
    }
  @Law
  inline fun maxLaw(a: Float, b: Float): Float =
    max(a, b).post({ it >= a && it >= b && (if (a >= b) it == a else it == b) }) {
      "bounds for max + definition"
    }
  @Law
  inline fun maxLaw(a: Double, b: Double): Double =
    max(a, b).post({ it >= a && it >= b && (if (a >= b) it == a else it == b) }) {
      "bounds for max + definition"
    }

  @Law
  inline fun minLaw(a: Int, b: Int): Int =
    min(a, b).post({ it <= a && it <= b && (if (a <= b) it == a else it == b) }) {
      "bounds for min + definition"
    }
  @Law
  inline fun minLaw(a: Long, b: Long): Long =
    min(a, b).post({ it <= a && it <= b && (if (a <= b) it == a else it == b) }) {
      "bounds for min + definition"
    }
  @Law
  inline fun minLaw(a: Float, b: Float): Float =
    min(a, b).post({ it <= a && it <= b && (if (a <= b) it == a else it == b) }) {
      "bounds for min + definition"
    }
  @Law
  inline fun minLaw(a: Double, b: Double): Double =
    min(a, b).post({ it <= a && it <= b && (if (a <= b) it == a else it == b) }) {
      "bounds for min + definition"
    }
}
