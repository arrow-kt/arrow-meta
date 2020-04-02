package arrow.meta.ide.plugins.proofs.markers

object CoercionTestCode {

  val twitterHandleDeclaration =
    """
      package consumer
      
      import arrow.Proof
      import arrow.Refined
      import arrow.TypeProof
      
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

  val code1 =
    """
      package consumer
      
      val implicit2: TwitterHandle? = "@eeeeeee"
    """.trimIndent()
}
