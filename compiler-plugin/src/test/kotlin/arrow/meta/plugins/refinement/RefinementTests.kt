package arrow.meta.plugins.refinement

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class RefinementTests {

  val prelude = """
    package test
    import arrow.*
  """.trimIndent()

  private fun twitterHandle(): String =
    """
    class TwitterHandle(val handle: String)  {
      companion object : Refined<String, TwitterHandle> {
        override val constructor = ::TwitterHandle
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
      TwitterHandle.from(this)
      
    """

  private fun positiveInt(): String =
    """
      inline class PositiveInt(val value: Int)  {
        companion object : Refined<Int, PositiveInt> {
          override val constructor = ::PositiveInt
          override val validate: Int.() -> Map<String, Boolean> = {
            mapOf(
              "Should be >= 0" to (this >= 0)
            )
          }
        }
      }
      
      @Proof(TypeProof.Extension, coerce = true)
      fun Int.positive(): PositiveInt? =
        PositiveInt.from(this)
    """


  private fun nonEmptyArray(): String =
    """
    inline class NonEmptyArray(val value: Array<Int>) {
      companion object : Refined<Array<Int>, NonEmptyArray> {
        override val constructor = ::NonEmptyArray
        override val validate: Array<Int>.() -> Map<String, Boolean> = {
          mapOf(
            "Should not be empty" to isNotEmpty()
          )
        }
      }
    }
    
    @Proof(TypeProof.Extension, coerce = true)
    fun Array<Int>.nonEmpty(): NonEmptyArray? =
      NonEmptyArray.from(this)
    """

  @Test
  fun `Construction is validated with arrays of literals`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |${nonEmptyArray()}
           |val x: NonEmptyArray = NonEmptyArray(emptyArray())
           |""".source
      },
      assert = {
        failsWith { it.contains("Should not be empty") }
      }
    ))
  }

  @Test
  fun `Construction is validated with predicates 1`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |${twitterHandle()}
           |val x: TwitterHandle = TwitterHandle("@admin")
           |""".source
      },
      assert = {
        failsWith { it.contains("Should not contain the word 'admin'") }
      }
    ))
  }

  @Test
  fun `Construction is validated with predicates PositiveInt with negative value`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |${positiveInt()}
           |val x: PositiveInt = PositiveInt(-1)
           |""".source
      },
      assert = {
        failsWith { it.contains("Should be >= 0") }
      }
    ))
  }

  @Test
  fun `Construction is validated with PositiveInt value`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |${positiveInt()}
           |val x: PositiveInt = PositiveInt(1)
           |""".source
      },
      assert = {
        "x".source.evalsTo(1)
      }
    ))
  }

  @Test
  fun `Runtime validation for nullable types coerces to null if invalid`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|$prelude
           |${twitterHandle()}
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
           |${twitterHandle()}
           |fun x(): TwitterHandle? = "@whatever"
           |""".source
      },
      assert = {
        "x()".source.evalsTo("@whatever")
      }
    ))
  }

}
