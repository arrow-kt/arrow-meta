package arrow.refinement.network

import arrow.refinement.Refined
import arrow.refinement.ensure
import kotlin.jvm.JvmInline

@JvmInline
value
/**
 * [UserPortNumber] constrains [Int] to be in range of 1024..49151
 *
 * # Safe nullable construction
 *
 * ```kotlin:ank
 * import arrow.refinement.network.UserPortNumber
 *
 * UserPortNumber.orNull(2000)
 * ```
 *
 * ```kotlin:ank
 * UserPortNumber.orNull(100000)
 * ```
 *
 * # Resolved constraints
 *
 * ```kotlin:ank
 * UserPortNumber.constraints(2000)
 * ```
 *
 * ```kotlin:ank
 * UserPortNumber.constraints(100000)
 * ```
 *
 *  ```kotlin:ank
 * UserPortNumber.isValid(2000)
 * ```
 *
 * ```kotlin:ank
 * UserPortNumber.isValid(100000)
 * ```
 *
 * # Folding validation
 *
 * ```kotlin:ank
 * UserPortNumber.fold(2000, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * ```kotlin:ank
 * UserPortNumber.fold(100000, { "failed: $it" }, { "success: $it" })
 * ```
 *
 * # Unsafe require
 *
 * ```kotlin:ank
 * try { UserPortNumber.require(100000) } catch (e: IllegalArgumentException) { e.message }
 * ```
 *
 * ```kotlin:ank
 * UserPortNumber.require(2000)
 * ```
 *
 */
class UserPortNumber private constructor(val value: Int) {
  companion object : Refined<Int, UserPortNumber>(::UserPortNumber, {
    ensure((it in 1024..49151) to "$it should be in the closed range of 1024..49151 to be a valid user port number")
  })
}
