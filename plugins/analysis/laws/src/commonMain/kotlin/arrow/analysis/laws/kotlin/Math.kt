// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Laws
import arrow.analysis.pre

/*
We follow the layout from https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib/src/kotlin
 */

object IntLaws : Laws {
  inline fun Int.divLaw(other: Int): Int {
    pre(other != 0) { "other is not zero" }
    return this / other
  }
}
