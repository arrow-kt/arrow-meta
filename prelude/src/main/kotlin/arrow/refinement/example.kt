package arrow

interface Refined<A, B> {
  val constructor: (A) -> B
  val validate: A.() -> Map<String, Boolean>
  fun validate(a: A): Map<String, Boolean> = validate.invoke(a)
  fun isValid(a: A): Boolean = validate(a).all { it.value }
  fun from(a: A) : B? =
    if (isValid(a)) constructor(a)
    else null
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Refinement(
  val predicate: String
)




