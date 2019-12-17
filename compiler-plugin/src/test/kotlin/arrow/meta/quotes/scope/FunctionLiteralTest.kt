package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.FunctionLiteralPlugin
import org.junit.Test

class FunctionLiteralTest  {

  @Test
  fun `Validate function literal scope properties`() {
    validate("""val a = { i: Int -> i + 1 }""".functionLiteral())
  }

  @Test
  fun `Validate function literal as an anonymous function`() {
    validate("""val increment: (Int) -> Unit = fun(x) { x + 1 }""".functionLiteral())
  }

  @Test
  fun `Validate function literal as a lambda expression`() {
    validate("""val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y }""".functionLiteral())
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(FunctionLiteralPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
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