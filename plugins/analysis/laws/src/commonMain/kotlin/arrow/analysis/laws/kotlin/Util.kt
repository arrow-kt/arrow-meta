// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.post

// run, with, apply, also, let
// are treated in a special way by the analysis

@Law inline fun Any?.hashCodeLaw(): Int =
  this.hashCode().post({ if (this == null) (it == 0) else true }) { "if null, hashcode is 0" }

@Law inline fun <T> lazyOfLaw(value: T): Lazy<T> =
  lazyOf(value).post({ it.value == value }) { "lazy value is argument" }

@Law inline fun TODOLaw(): Nothing =
  TODO().post({ false }) { "nothing executes after TODO" }

@Law inline fun TODOLaw(reason: String): Nothing =
  TODO(reason).post({ false }) { "nothing executes after TODO" }

@Law inline fun <A, B> A.toLaw(other: B): Pair<A, B> =
  (this to other).post({ it.first == this && it.second == other}) { "components" }
