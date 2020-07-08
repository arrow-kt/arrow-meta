package arrow.meta.ide.plugins.proofs.annotators

object GivenAnnotatorTestCode {

  val givenPrelude =
    """
      package arrow
      
      @Retention(AnnotationRetention.RUNTIME)
      @Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.TYPE_PARAMETER,
        AnnotationTarget.TYPE
      )
      @MustBeDocumented
      annotation class Given
    """.trimIndent()

  val givenProviders =
    """
      package consumer
      
      import arrow.Given

      @Given
      val x: String = "yes!"

      @Given
      val y: Int = 47
    """.trimIndent()

  val code1 =
    """
      package test

      import arrow.Given

      fun exampleFun1(
          text: @Given String,
          notGiven: Int,
          int47: @Given Int
      ): Unit = TODO()
    """.trimIndent()

  val code2 =
    """
      package test

      import arrow.given
      
      fun exampleFun2(
          text: String = given(),
          notGiven: Int,
          int47: Int = given()
      ): String = given<String>() + given<Int>()
    """.trimIndent()

  val code3 =
    """
      package test

      import arrow.given

      fun exampleFun3(
          text: String
      ): String = given<String>()
    """.trimIndent()
}
