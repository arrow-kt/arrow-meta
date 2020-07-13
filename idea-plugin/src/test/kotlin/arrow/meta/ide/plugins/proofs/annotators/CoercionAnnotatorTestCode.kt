package arrow.meta.ide.plugins.proofs.annotators

object CoercionAnnotatorTestCode {

  val code1 =
    """
      package test

      import consumer.TwitterHandle
      
      val implicitCoercion: TwitterHandle? = "@danieeehh"
    """.trimIndent()

  val code2 =
    """
      package test

      import consumer.TwitterHandle
      
      val implicitCoercion: String = TwitterHandle("@aballano")
    """.trimIndent()

  val code3 =
    """
      package test

      import consumer.TwitterHandle
      
      fun print(s: String, i: Int, s2: TwitterHandle?) = println(s)
      
      fun implicitCoercions() =
          print(TwitterHandle("@aballano"), 1, "@danieeehh")
    """.trimIndent()
}
