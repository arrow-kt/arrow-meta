package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ReturnExpressionPlugin
import org.junit.Test

class ReturnExpressionTest {

  private val returnExpression = """
                         | //metadebug
                         | 
                         | fun whatTimeIsIt(): Long {
                         |   return System.currentTimeMillis()
                         | }
                         | """.trimMargin().source

  @Test
  fun `Validate return expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ReturnExpressionPlugin())) },
      code = { returnExpression },
      assert = { quoteOutputMatches(returnExpression) }
    ))
  }
}