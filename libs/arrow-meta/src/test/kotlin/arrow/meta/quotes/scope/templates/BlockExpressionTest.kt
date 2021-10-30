package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.BlockExpressionPlugin
import org.junit.jupiter.api.Test

class BlockExpressionTest {

  companion object {
    val blockExpression =
      """
                         | //metadebug
                         | 
                         | val x = {}
                         | """.source
  }

  @Test
  fun `Validate block expression scope properties`() {
    assertThis(
      CompilerTest(
        config = { listOf(addMetaPlugins(BlockExpressionPlugin())) },
        code = { blockExpression },
        assert = { quoteOutputMatches(blockExpression) }
      )
    )
  }
}
