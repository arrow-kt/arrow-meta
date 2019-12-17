package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ThrowExpressionPlugin
import org.junit.Test

class ThrowExpressionTest  {

  private val throwExpression = """
                         | //metadebug
                         | 
                         | fun throwAssertionError() {
                         |   throw AssertionError()
                         | }
                         | """.source

  @Test
  fun `Validate throw expression properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ThrowExpressionPlugin())) },
      code = { throwExpression },
      assert = { quoteOutputMatches(throwExpression) }
    ))
  }
}
