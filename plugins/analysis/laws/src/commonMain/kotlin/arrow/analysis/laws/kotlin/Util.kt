// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/*
We follow the layout from
https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib/src/kotlin/util
except for functions related to numbers
 */

// ** HashCode.kt **

@Law
inline fun Any?.hashCodeLaw(): Int =
  this.hashCode().post({ if (this == null) (it == 0) else true }) { "if null, hashcode is 0" }

// ** Lazy.kt **

@Law
inline fun <T> lazyOfLaw(value: T): Lazy<T> =
  lazyOf(value).post({ it.value == value }) { "lazy value is argument" }

// ** Preconditions.kt **

// require is treated in a special way by the analysis

// ** Result.kt **

object ResultLaws : Laws {
  // we define isFailure in terms of isSuccess,
  // but not the other way around,
  // to prevent infinite recursion in the solver
  inline fun <T> Result<T>.isFailureLaw(): Boolean =
    this.isFailure.post({ it == !this.isSuccess }) { "failure is opposite to success" }

  inline fun <T> Result<T>.getOrNullLaw(): T? =
    this.getOrNull().post({ result ->
      (this.isSuccess) xor (result == null)
    }) { "null if failure" }

  inline fun <T> successLaw(x: T): Result<T> =
    success(x).post({ it.isSuccess == true }) { "create a success" }

  inline fun <T> failureLaw(e: Throwable): Result<T> =
    failure<T>(e).post({ it.isFailure == true }) { "create a failure" }

  inline fun <R, T> Result<T>.mapLaw(transform: (value: T) -> R): Result<R> =
    this.map(transform).post({ it.isSuccess == this.isSuccess }) { "map preserves success" }
}

// ** Standard.kt **

@Law
inline fun TODOLaw(): Nothing =
  TODO().post({ false }) { "nothing executes after TODO" }

@Law
inline fun TODOLaw(reason: String): Nothing =
  TODO(reason).post({ false }) { "nothing executes after TODO" }

// run, with, apply, also, let
// are treated in a special way by the analysis

// ** Tuples.kt **

@Law
inline fun <A, B> A.toLaw(other: B): Pair<A, B> =
  (this to other).post({ it.first == this && it.second == other}) { "components" }
