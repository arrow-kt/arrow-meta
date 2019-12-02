package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.BinaryExpressionPlugin
import org.junit.Test

class BinaryExpressionTest {

  private val binaryExpression = """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |   init {
                         |     println(2 == 3)
                         |   }
                         | }
                         | """.source

  @Test
  fun `Validate when binary expression properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(BinaryExpressionPlugin())) },
      code = { binaryExpression },
      assert = { quoteOutputMatches(binaryExpression) }
    ))
  }
}

