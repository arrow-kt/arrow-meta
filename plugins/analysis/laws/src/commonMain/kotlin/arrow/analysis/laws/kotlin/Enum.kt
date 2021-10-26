// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post

@Laws object EnumLaws {
  @Law inline fun <E : Enum<E>> Enum<E>.ordinalLaw(): Int =
    ordinal.post({ it >= 0 }) { "ordinal is non-negative" }
}
