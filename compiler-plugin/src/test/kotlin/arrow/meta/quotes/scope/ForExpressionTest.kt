package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ForExpressionPlugin
import org.junit.Test

class ForExpressionTest {

  private val forExpression = """
                         | //metadebug
                         | 
                         | fun someFunction() {
                         |   for (i in 1..10) {
                         |     println(i)
                         |   }
                         | }
                         | """.source

  @Test
  fun `Validate for expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ForExpressionPlugin())) },
      code = { forExpression },
      assert = { quoteOutputMatches(forExpression) }
    ))
  }
}