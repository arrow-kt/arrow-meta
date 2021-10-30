package arrow.meta.plugins.proofs

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Test

class ContextTest {

  @Test
  fun `multiple context providers are supported`() {
    givenTest(
      source =
        """
        @Given object X {
          val value = "yes!"
        }
        @Config object Y {
          val value = "nope!"
        }
        fun foo(@Given x: X, @Config y: Y): Pair<String, String> =
          x.value to y.value
        val result = foo()
      """,
      expected = "result" to ("yes!" to "nope!")
    )
  }

  @Test
  fun `different context providers of the same type are not ambiguous`() {
    givenTest(
      source =
        """
        @Given internal val x: String = "yes!"
        @Config internal val y: String = "nope!"
        fun foo(@Given x: String, @Config y: String): Pair<String, String> =
          x to y
        val result = foo()
      """,
      expected = "result" to ("yes!" to "nope!")
    )
  }

  @Test
  fun `A provider supports multiple contexts`() {
    givenTest(
      source =
        """
        @Given @Config internal val x: String = "yes!"
        fun foo(@Given x: String, @Config y: String): Pair<String, String> =
          x to y
        val result = foo()
      """,
      expected = "result" to ("yes!" to "yes!")
    )
  }

  val prelude =
    """
    package test
    import arrow.Context
    
    @Context
    @Retention(AnnotationRetention.RUNTIME)
    @Target(
      AnnotationTarget.CLASS,
      AnnotationTarget.FUNCTION,
      AnnotationTarget.PROPERTY,
      AnnotationTarget.VALUE_PARAMETER
    )
    @MustBeDocumented
    annotation class Given
    
    @Context
    @Retention(AnnotationRetention.RUNTIME)
    @Target(
      AnnotationTarget.CLASS,
      AnnotationTarget.FUNCTION,
      AnnotationTarget.PROPERTY,
      AnnotationTarget.VALUE_PARAMETER
    )
    @MustBeDocumented
    annotation class Config
      
    //metadebug
  """.trimIndent()

  private fun givenTest(source: String, expected: Pair<String, Any?>) {
    val codeSnippet = """
       $prelude
       $source
      """
    assertThis(
      CompilerTest(
        config = { newMetaDependencies() },
        code = { codeSnippet.source },
        assert = { allOf(expected.first.source.evalsTo(expected.second)) }
      )
    )
  }
}
