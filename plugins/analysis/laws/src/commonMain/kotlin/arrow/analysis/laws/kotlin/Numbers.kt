// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Laws
import arrow.analysis.post
import arrow.analysis.pre

object ByteLaws : Laws {
  inline fun Byte.divLaw(other: Int): Int {
    pre(other != 0) { "other is not zero" }
    return this / other
  }
  inline fun Byte.divLaw(other: Long): Long {
    pre(other != 0L) { "other is not zero" }
    return this / other
  }

  inline fun Byte.countOneBitsLaw(): Int =
    this.countOneBits().post({ it >= 0 }) { "number of ones is >= 0" }

  inline fun Byte.countLeadingZeroBitsLaw(): Int =
    this.countLeadingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }

  inline fun Byte.countTrailingZeroBitsLaw(): Int =
    this.countTrailingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }
}

object ShortLaws : Laws {
  inline fun Short.divLaw(other: Int): Int {
    pre(other != 0) { "other is not zero" }
    return this / other
  }
  inline fun Short.divLaw(other: Long): Long {
    pre(other != 0L) { "other is not zero" }
    return this / other
  }

  inline fun Short.countOneBitsLaw(): Int =
    this.countOneBits().post({ it >= 0 }) { "number of ones is >= 0" }

  inline fun Short.countLeadingZeroBitsLaw(): Int =
    this.countLeadingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }

  inline fun Short.countTrailingZeroBitsLaw(): Int =
    this.countTrailingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }
}

object IntLaws : Laws {
  inline fun Int.divLaw(other: Int): Int {
    pre(other != 0) { "other is not zero" }
    return this / other
  }
  inline fun Int.divLaw(other: Long): Long {
    pre(other != 0L) { "other is not zero" }
    return this / other
  }

  inline fun Int.countOneBitsLaw(): Int =
    this.countOneBits().post({ it >= 0 }) { "number of ones is >= 0" }

  inline fun Int.countLeadingZeroBitsLaw(): Int =
    this.countLeadingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }

  inline fun Int.countTrailingZeroBitsLaw(): Int =
    this.countTrailingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }
}

object LongLaws : Laws {
  inline fun Long.divLaw(other: Int): Long {
    pre(other != 0) { "other is not zero" }
    return this / other
  }
  inline fun Long.divLaw(other: Long): Long {
    pre(other != 0L) { "other is not zero" }
    return this / other
  }

  inline fun Long.countOneBitsLaw(): Int =
    this.countOneBits().post({ it >= 0 }) { "number of ones is >= 0" }

  inline fun Long.countLeadingZeroBitsLaw(): Int =
    this.countLeadingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }

  inline fun Long.countTrailingZeroBitsLaw(): Int =
    this.countTrailingZeroBits().post({ it >= 0 }) { "number of zeros is >= 0" }
}
