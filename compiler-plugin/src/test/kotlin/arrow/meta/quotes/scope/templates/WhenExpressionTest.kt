package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhenExpressionPlugin
import org.junit.Ignore
import org.junit.Test

// TODO failing compilation:  Expecting a when-condition, Expecting an expression, is-condition or in-condition

class WhenExpressionTest  {

  private val whenExpression = """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |   fun doMaths(x: Int) {
                         |     when {
                         |       x + 2 == 4 -> { println("I can do maths") }
                         |       else -> { println("I cannot do maths") }
                         |     }
                         |   }
                         | }
                         | """.source

  @Ignore
  @Test
  fun `Validate when expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(WhenExpressionPlugin())) },
      code = { whenExpression },
      assert = { quoteOutputMatches(whenExpression) }
    ))
  }
}