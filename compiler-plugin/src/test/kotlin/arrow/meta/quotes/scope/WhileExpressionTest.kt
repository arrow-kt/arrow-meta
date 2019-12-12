package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhileExpressionPlugin
import io.kotlintest.specs.AnnotationSpec

class WhileExpressionTest : AnnotationSpec() {

  private val whileExpression = """
                         | //metadebug
                         | 
                         | fun power(x: Int) {
                         |   var y = 0
                         |   while (y++ < x) {
                         |     println("INFINITE POWER")
                         |   }
                         | }
                         | """.source

  @Test
  fun `Validate while expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(WhileExpressionPlugin())) },
      code = { whileExpression },
      assert = { quoteOutputMatches(whileExpression) }
    ))
  }
}
