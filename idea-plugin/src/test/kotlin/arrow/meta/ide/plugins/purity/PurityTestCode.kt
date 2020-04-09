package arrow.meta.ide.plugins.purity

object PurityTestCode {
  val code1: String
    get() =
      """
      fun hello(): Unit = println("Hello!")
      
      fun nested1(): String =
        "Hello".also {
          println(it)
        }
      
      fun nested2(): String =
        "Hello".also { hello ->
          21.also {
            it.run {
              println(hello)
            }
          }
        }
     """.trimIndent()
}

