package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.LambdaExpressionsPlugin
import arrow.meta.quotes.scope.templates.LambdaExpressionTest.Companion.lambdaExpression
import org.junit.jupiter.api.Test

class LambdaExpressionTest  {

  companion object {
    private val lambdaExpression = """val square: (Int) -> Int = { x -> x * x }""".lambdaExpression()

    private val lambdaExpressionAsFunction = """
      | fun whenPassingALambdaLiteral_thenCallTriggerLambda() {
      |   fun invokeLambda(lambda: (Double) -> Boolean) : Boolean {
      |     return lambda(4.329)
      |   }
      |   
      |   val result = invokeLambda({
      |     true
      |   })
      | }
      |""".lambdaExpression()

    private val lambdaExpressionWithMultipleParameters = """val square: (Int, Int, Int) -> Int = { x, y, _ -> x + y }""".lambdaExpression()

    val lambdaExpressions = arrayOf(
      lambdaExpression,
      lambdaExpressionAsFunction,
      lambdaExpressionWithMultipleParameters
    )

    private fun String.lambdaExpression(): Code.Source {
      return """
      | //metadebug
      | 
      | $this
      | """.source
    }
  }

  @Test
  fun `Validate lambda expression scope properties`() {
    validate(lambdaExpression)
  }

  @Test
  fun `Validate lambda expression as a function literal`() {
    validate(lambdaExpressionAsFunction)
  }

  @Test
  fun `Validate lambda expression with multiple parameters`() {
    validate(lambdaExpressionWithMultipleParameters)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(LambdaExpressionsPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}
