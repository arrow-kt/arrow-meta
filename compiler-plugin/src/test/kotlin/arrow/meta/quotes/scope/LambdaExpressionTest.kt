package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.LambdaExpressionsPlugin
import org.junit.Test

class LambdaExpressionTest {

  @Test
  fun `Validate lambda expression scope properties`() {
    validate("""val square: (Int) -> Int = { x -> x * x }""".lambdaExpression())
  }

  @Test
  fun `Validate lambda expression as a function literal`() {
    validate("""
      | fun whenPassingALambdaLiteral_thenCallTriggerLambda() {
      |   fun invokeLambda(lambda: (Double) -> Boolean) : Boolean {
      |     return lambda(4.329)
      |   }
      |   
      |   val result = invokeLambda({
      |     true
      |   })
      | }
      |""".lambdaExpression())
  }

  @Test
  fun `Validate lambda expression with multiple parameters`() {
    // TODO
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(LambdaExpressionsPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }

  private fun String.lambdaExpression(): Code.Source {
    return """
      | //metadebug
      | 
      | class Wrapper {
      |   init {
      |    $this
      |   }
      |}  
      | """.trimMargin().trim().source
  }
}
