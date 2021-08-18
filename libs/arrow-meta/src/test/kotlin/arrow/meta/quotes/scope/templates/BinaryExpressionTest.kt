package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.BinaryExpressionPlugin
import org.junit.jupiter.api.Test

class BinaryExpressionTest {

  companion object {
    val binaryExpression = """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |   init {
                         |     println(2 == 3)
                         |   }
                         | }
                         | """.source
  }

  @Test
  fun `Validate when binary expression properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(BinaryExpressionPlugin())) },
      code = { binaryExpression },
      assert = { quoteOutputMatches(binaryExpression) }
    ))
  }
}
