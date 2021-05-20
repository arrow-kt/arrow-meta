package arrow.macros

import kotlin.reflect.KClass

/**
 * Compile time only
 */
data class Inline(val value: Any?, val type: KClass<*>) : RuntimeException()
data class Error(val msg: String) : RuntimeException(msg)

inline fun <reified A> inline(value: A): Nothing =
  throw Inline(value, A::class)

inline fun error(msg: String): Nothing =
  throw Error(msg)

@Retention(AnnotationRetention.SOURCE)
@Target(
  AnnotationTarget.VALUE_PARAMETER
)
@MustBeDocumented
annotation class transparent


/**
 * When evaluated at compile time replaces call sites like
 * ```kotlin
 * val n: Int = foo(2)
 * ```
 * for
 * ```kotlin
 * val n : Int = 2 / 2
 * ```
 */
inline fun <reified A> foo(@transparent x: A): A =
  when (x) {
    is Int -> inline(x / 2)
    is String -> inline(x.substring(0, x.length / 2))
    else -> error("unsupported instance for $x")
  }

object Test {
  /**
   * gets replaced at compile time for
   * `val n: Int = 2 / 2`
   **/
  val n = foo(2)

  /**
   * gets replaced at compile time for
   * `val s: String = "test".substring(0, "test".length / 2)`
   **/
  val s = foo("test")

  val z = foo(n)
}


fun main() {
  Test.z
}
