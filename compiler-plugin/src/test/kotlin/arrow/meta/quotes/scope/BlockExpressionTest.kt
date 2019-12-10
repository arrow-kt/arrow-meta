package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.BlockExpressionPlugin
import io.kotlintest.specs.AnnotationSpec

class BlockExpressionTest : AnnotationSpec() {

  private val blockExpression = """
                         | //metadebug
                         | 
                         | val x = {}
                         | """.source

  @Test
  fun `Validate block expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(BlockExpressionPlugin())) },
      code = { blockExpression },
      assert = { quoteOutputMatches(blockExpression) }
    ))
  }
}