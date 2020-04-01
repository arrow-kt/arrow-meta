package arrow.meta.ide.plugins.proofs.markers

object CoercionTestCode {
  val code1 =
    """
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
      
      enum class TypeProof {
        Extension,
        Refinement,
        Negation
      }
      
      @Retention(AnnotationRetention.RUNTIME)
      @Target(AnnotationTarget.FUNCTION)
      @MustBeDocumented
      annotation class Proof(
        val of: TypeProof,
        val refined: Array<String> = [],
        val coerce: Boolean = true,
        val inductive: Boolean = false
      )
      
      inline class TwitterHandle(val handle: String) {
          companion object : Refined<String, TwitterHandle> {
              override val constructor = ::TwitterHandle
              override val validate: String.() -> Map<String, Boolean> = {
                  mapOf(
                      "Should start with '@'" to startsWith("@"),
                      "Should have length <= 16" to (length <= 16),
                      "Should have length > 2" to (length > 2),
                      "Should not contain the word 'twitter'" to !contains("twitter"),
                      "Should not contain the word 'admin'" to !contains("admin")
                  )
              }
          }
      }
      
      @Proof(TypeProof.Extension, coerce = true)
      fun String.twitterHandle(): TwitterHandle? =
          TwitterHandle.from(this)
      
      @Proof(TypeProof.Extension, coerce = true)
      fun TwitterHandle.handle(): String =
          handle
      
      val implicit: TwitterHandle? = "@eeeeeee"
    """.trimIndent()
}
