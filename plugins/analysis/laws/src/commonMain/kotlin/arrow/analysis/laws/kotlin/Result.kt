// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Laws
import arrow.analysis.post
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

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
