package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.FunctionLiteralPlugin
import org.junit.jupiter.api.Test

class FunctionLiteralTest  {

  companion object {
    private val functionLiteral = """val a = { i: Int -> i + 1 }""".functionLiteral()

    private val anonymousFunction = """val increment: (Int) -> Unit = fun(x) { x + 1 }""".functionLiteral()

    private val lambdaExpression = """val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y }""".functionLiteral()

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

    val functionalLiteralExpressions = arrayOf(
      functionLiteral,
      anonymousFunction,
      lambdaExpression
    )
  }

  @Test
  fun `Validate function literal scope properties`() {
    validate(functionLiteral)
  }

  @Test
  fun `Validate function literal as an anonymous function`() {
    validate(anonymousFunction)
  }

  @Test
  fun `Validate function literal as a lambda expression`() {
    validate(lambdaExpression)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(FunctionLiteralPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}