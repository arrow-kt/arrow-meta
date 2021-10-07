// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Laws
import arrow.analysis.post

object ComparableRangeLaws : Laws {
  inline fun <T : Comparable<T>> T.rangeToLaw(that: T): ClosedRange<T> =
    this.rangeTo(that).post({ it.start == this && it.endInclusive == that }) { "range with given bounds" }
}
