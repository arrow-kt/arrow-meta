package arrow.meta.plugins.refinement

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class RefinementTests {

  @Test
  fun `Construction is validated with arrays of literals`() {
    refinementTest(
      """
           |$nonEmptyArray
           |val x: NonEmptyArray = NonEmptyArray(emptyArray())
           |""",
      assert = {
        failsWith { it.contains("Should not be empty") }
      }
    )
  }

  @Test
  fun `Construction is validated with predicates 1`() {
    refinementTest(
      """
           |${twitterHandle()}
           |val x: TwitterHandle = TwitterHandle("@admin")
           |""",
      assert = {
        failsWith { it.contains("Should not contain the word 'admin'") }
      }
    )
  }

  @Test
  fun `Construction is validated with predicates PositiveInt with negative value`() {
    refinementTest(
      """
           |$positiveInt
           |val x: PositiveInt = PositiveInt(-1)
           |""",
      assert = {
        failsWith { it.contains("Should be >= 0") }
      }
    )
  }

  @Test
  fun `Construction is validated with PositiveInt value`() {
    refinementTest(
      """
           |$positiveInt
           |val x: PositiveInt = PositiveInt(1)
           |""",
      assert = {
        "x".source.evalsTo(1)
      }
    )
  }

  @Test
  fun `Runtime validation for nullable types coerces to null if invalid`() {
    refinementTest(
      """
           |${twitterHandle()}
           |val x: TwitterHandle? = "@admin"
           |""",
      assert = {
        "x".source.evalsTo(null)
      }
    )
  }

  @Test
  fun `Runtime validation for nullable types accepts if valid`() {
    refinementTest(
      """
           |${twitterHandle()}
           |fun x(): TwitterHandle? = "@whatever"
           |val result = x()?.handle
           |""",
      assert = {
        "result".source.evalsTo("@whatever")
      }
    )
  }

  fun refinementTest(src: String, assert: CompilerTest.Companion.() -> Assert): Unit {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """
    |package test
    |
    |import arrow.Refined
    |import arrow.Refinement
    |import arrow.Coercion
    |
    |$src
    """.source
      },
      assert = assert
    ))
  }

  fun twitterHandle(): String =
    """
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
    """

  val positiveInt: String =
    """
      inline class PositiveInt(val value: Int)  {
        companion object : Refined<Int, PositiveInt> {
          override val target : (Int) -> PositiveInt = ::PositiveInt
          override val validate: Int.() -> Map<String, Boolean> = {
            mapOf(
              "Should be >= 0" to (this >= 0)
            )
          }
        }
      }
      
      @Coercion
      fun Int.positive(): PositiveInt? =
        PositiveInt.from(this)
    """

  val nonEmptyArray: String =
    """
    @Refinement 
    inline class NonEmptyArray(val value: Array<Int>) {
      companion object : Refined<Array<Int>, NonEmptyArray> {
        override val target: (Array<Int>) -> NonEmptyArray = ::NonEmptyArray
        override val validate: Array<Int>.() -> Map<String, Boolean> = {
          mapOf(
            "Should not be empty" to isNotEmpty()
          )
        }
      }
    }
    
    @Coercion
    fun Array<Int>.nonEmpty(): NonEmptyArray? =
      NonEmptyArray.from(this)
    """
}
