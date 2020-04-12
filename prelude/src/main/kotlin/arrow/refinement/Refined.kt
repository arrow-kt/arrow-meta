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



@Refinement class TwitterHandle(val handle: String)  {
  companion object : Refined<String, TwitterHandle> {
    override val target: (String) -> TwitterHandle = ::TwitterHandle
    override val validate: String.() -> Map<String, Boolean> = {
      mapOf(
        "Should start with '@'" to startsWith("@"),
        "Should have length <= 16" to (length <= 16),
        "Should not contain the word 'twitter'" to !contains("twitter"),
        "Should not contain the word 'admin'" to !contains("admin")
      )
    }
  }
}