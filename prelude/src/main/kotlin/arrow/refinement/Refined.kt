package arrow

interface Refined<A, B> {
  val target: (A) -> B
  val validate: A.() -> Map<String, Boolean>
  fun validate(a: A): Map<String, Boolean> = validate.invoke(a)
  fun isValid(a: A): Boolean = validate(a).all { it.value }
  fun from(a: A) : B? =
    if (isValid(a)) target(a)
    else null
}