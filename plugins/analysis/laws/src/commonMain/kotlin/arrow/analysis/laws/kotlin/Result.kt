// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post

@Laws object ResultLaws {
  // we define isFailure in terms of isSuccess,
  // but not the other way around,
  // to prevent infinite recursion in the solver
  @Law inline fun <T> Result<T>.isFailureLaw(): Boolean =
    this.isFailure.post({ it == !this.isSuccess }) { "failure is opposite to success" }

  @Law inline fun <T> Result<T>.getOrNullLaw(): T? =
    this.getOrNull().post({ result ->
      (this.isSuccess) xor (result == null)
    }) { "null if failure" }

  @Law inline fun <T> Result.Companion.successLaw(x: T): Result<T> =
    success(x).post({ it.isSuccess == true }) { "create a success" }

  @Law inline fun <T> Result.Companion.failureLaw(e: Throwable): Result<T> =
    failure<T>(e).post({ it.isFailure == true }) { "create a failure" }

  @Law inline fun <R, T> Result<T>.mapLaw(transform: (value: T) -> R): Result<R> =
    this.map(transform).post({ it.isSuccess == this.isSuccess }) { "map preserves success" }
}
