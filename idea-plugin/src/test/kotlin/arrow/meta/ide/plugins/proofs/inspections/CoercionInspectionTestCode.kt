package arrow.meta.ide.plugins.proofs.inspections

object CoercionInspectionTestCode {

  val prelude =
    """
      package arrow

      @Retention(AnnotationRetention.RUNTIME)
      @Target(AnnotationTarget.FUNCTION)
      @MustBeDocumented
      annotation class Coercion

      interface Refined<A, B> {
        val target: (A) -> B
        val validate: A.() -> Map<String, Boolean>
        fun validate(a: A): Map<String, Boolean> = validate.invoke(a)
        fun isValid(a: A): Boolean = validate(a).all { it.value }
        fun from(a: A) : B? =
          if (isValid(a)) target(a)
          else null
      }

      @Retention(AnnotationRetention.RUNTIME)
      @Target(AnnotationTarget.CLASS)
      @MustBeDocumented
      annotation class Refinement

      @Retention(AnnotationRetention.RUNTIME)
      @Target(AnnotationTarget.CLASS)
      annotation class RefinedBy(val value: String)
    """.trimIndent()

  val twitterHandleDeclaration =
    """
      package consumer
      
      import arrow.Coercion
      import arrow.Refined
      import arrow.Refinement
      
      @Refinement
      inline class TwitterHandle(val handle: String) {
          companion object : Refined<String, TwitterHandle> {
              override val target = ::TwitterHandle
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
      
      @arrow.Coercion
      fun String.twitterHandle(): TwitterHandle? =
          TwitterHandle.from(this)
      
      @arrow.Coercion
      fun TwitterHandle.handle(): String =
          handle
    """.trimIndent()

  val code1 =
    """
      package test

      import consumer.TwitterHandle
      
      val coercion: TwitterHandle? = "@danieeehh"
    """.trimIndent()

  val code1_after_fix =
    """
      package test

      import consumer.TwitterHandle
      import consumer.twitterHandle
      
      val coercion: TwitterHandle? = "@danieeehh".twitterHandle()
    """.trimIndent()

  val code2 =
    """
      package test

      import consumer.TwitterHandle
      import consumer.twitterHandle
      
      val coercion: TwitterHandle? = "@danieeehh".twitterHandle()
    """.trimIndent()

  val code2_after_fix =
    """
      package test

      import consumer.TwitterHandle
      import consumer.twitterHandle
      
      val coercion: TwitterHandle? = "@danieeehh"
    """.trimIndent()

  val code3 =
    """
      package test

      import consumer.TwitterHandle
      
      fun print(s: String, i: Int, s2: TwitterHandle?) = println(s)
      
      fun implicitExplicitCoercions() =
          print(TwitterHandle("@aballano"), 1, "@aballano")
    """.trimIndent()

  val code3_after_fix =
    """
      package test

      import consumer.TwitterHandle
      import consumer.handle
      
      fun print(s: String, i: Int, s2: TwitterHandle?) = println(s)
      
      fun implicitExplicitCoercions() =
          print(TwitterHandle("@aballano").handle(), 1, "@aballano")
    """.trimIndent()
}
