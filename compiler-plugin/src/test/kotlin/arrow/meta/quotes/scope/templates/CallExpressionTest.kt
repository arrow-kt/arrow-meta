package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.CallExpressionPlugin
import org.junit.Test

class CallExpressionTest {
  companion object {
    private val callExpression = """
      | println()
      | """.callExpression()

    private val oneParameter = """
      | println("wassup?")
      | """.callExpression()

    private val multipleParameters = """
      | fun multipleParams(one: String, two: String) {
      |   println(one)
      |   println(two)
      | }
      |
      | multipleParams("yo", "wassup?")
      | """.callExpression()

    private fun String.callExpression(): String = """
      | //metadebug
      |
      | class Wrapper {
      |   fun whatever() {
      |     $this
      |   }
      | }
      |"""
  }

  @Test
  fun `Validate call expression scope properties`() {
    callExpression.verify
  }

  @Test
  fun `Validate call expression with one parameter scope properties`() {
    oneParameter.verify
  }

  @Test
  fun `Validate call expression with multiple parameters scope properties`() {
    multipleParameters.verify
  }

  private val String.verify
    get() = assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(CallExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
}

