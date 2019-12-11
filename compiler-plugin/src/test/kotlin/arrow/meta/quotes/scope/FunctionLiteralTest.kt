package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.FunctionLiteralPlugin
import io.kotlintest.specs.AnnotationSpec

/**
 * Function Literals are a little unclear - see more here: https://kotlinlang.org/docs/reference/lambdas.html
 */
class FunctionLiteralTest : AnnotationSpec() {

  @Test
  fun `Validate function literal scope properties`() {
    validate("""val a = { i: Int -> i + 1 }""".functionLiteral())
  }

  @Test
  fun `Validate function literal as an anonymous function`() {
    // TODO
  }

  @Test
  fun `Validate function literal as a lambda expression`() {
    validate("""val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y }""".functionLiteral())
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(FunctionLiteralPlugin())) },
      code = { source },
      assert = { compiles } //quoteOutputMatches(source) }
    ))
  }

  private fun String.functionLiteral(): Code.Source {
    return """
      | //metadebug
      | 
      | class Wrapper {
      |   fun whatever() {
      |    $this
      |   }
      |  }
      | """.trimMargin().trim().source
  }
}

class Wrapper {
  fun whatever() {
    val a = { i: Int -> i + 1 }
  }
}