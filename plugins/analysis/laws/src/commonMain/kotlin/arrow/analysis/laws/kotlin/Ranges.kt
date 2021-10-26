// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post

@Laws object ComparableRangeLaws {
  @Law inline fun <T : Comparable<T>> T.rangeToLaw(that: T): ClosedRange<T> =
    this.rangeTo(that).post({ it.start == this && it.endInclusive == that }) { "range with given bounds" }
}
