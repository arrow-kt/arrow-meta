package arrow.meta.ide.plugins.lens

object LensTestCode {
  val code1 =
    """
    data class TestLenses(val a: String, val b: String)
    
    data class Person(
      val name: String,
      val age: Int,
      val home: Home
    )
    
    data class Home(val street: String)
    """.trimIndent()
}