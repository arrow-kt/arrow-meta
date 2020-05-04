package arrow.meta.ide.plugins.proofs.inspections

object CoercionInspectionTestCode {

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
