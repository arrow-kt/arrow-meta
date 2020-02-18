package arrow.meta.plugins.refinement

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class RefinementTests {

  val prelude = """
    package test
    import arrow.*
    
    inline class TwitterHandle(val handle: String)  {
      companion object : Refined<String> {
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
    
    //Similar extensions can target Validation and Either not just nullable types
    @Proof(TypeProof.Extension, coerce = true)
    fun String.twitterHandle(): TwitterHandle? =
      if (TwitterHandle.validate(this).values.all { true }) TwitterHandle(this)
      else null
      
  """.trimIndent()

  @Test
  fun `Construction is validated with predicates`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |
           |val x: TwitterHandle = TwitterHandle("@admin")
           |""".source
      },
      assert = {
        failsWith { it.contains("Should not contain the word 'admin'") }
      }
    ))
  }

  @Test
  fun `Runtime validation for nullable types coerces to null if invalid`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |
           |val x: TwitterHandle? = "@admin"
           |""".source
      },
      assert = {
        "x".source.evalsTo(null)
      }
    ))
  }

  @Test
  fun `Runtime validation for nullable types accepts if valid`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |
           |val x: TwitterHandle? = "@whatever"
           |""".source
      },
      assert = {
        "x".source.evalsTo("@whatever")
      }
    ))
  }

}
