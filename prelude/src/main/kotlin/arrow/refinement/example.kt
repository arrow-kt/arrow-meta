package arrow

interface Refined<A> {
  val validate: A.() -> Map<String, Boolean>
  fun validate(a: A): Map<String, Boolean> = validate.invoke(a)
  fun isValid(a: A): Boolean = validate(a).all { it.value }
  operator fun <B> invoke(a: A, f: (A) -> B) : B? =
    if (isValid(a)) f(a)
    else null
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Refinement(
  val predicate: String
)


inline class NonEmptyArray(val value: Array<Int>) {
  companion object : Refined<Array<Int>> {
    override val validate: Array<Int>.() -> Map<String, Boolean> = {
      mapOf(
        "Should not be empty" to isNotEmpty()
      )
    }
  }
}




