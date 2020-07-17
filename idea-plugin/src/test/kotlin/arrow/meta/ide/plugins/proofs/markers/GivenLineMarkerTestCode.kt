package arrow.meta.ide.plugins.proofs.markers

object GivenLineMarkerTestCode {

  val code1 =
    """
      package test

      import arrow.Given

      @Given
      val x: String = "yes!"
      
      @Given
      val y: Int = 47
    """.trimIndent()

  val code2 =
    """
      package test

      import arrow.Given
      
      @Given
      fun givenFun(): String = TODO()
    """.trimIndent()

  val code3 =
    """
      package test

      import arrow.Given
      
      @Given
      object givenObject
    """.trimIndent()

  val code4 =
    """
      package test

      import arrow.Given
      
      @Given
      class GivenClass()
    """.trimIndent()
}