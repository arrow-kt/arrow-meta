package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.TryExpressionPlugin
import io.kotlintest.specs.AnnotationSpec

class TryExpressionTest : AnnotationSpec() {

  private val tryExpression = """
                         | //metadebug
                         | 
                         | fun measureTimeMillis(block: () -> Unit): Unit {
                         |    try {
                         |      block()
                         |    } catch (throwable: Throwable) { println(throwable) }
                         |  }
                         | """.source

  @Test
  fun `Validate try expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(TryExpressionPlugin())) },
      code = { tryExpression },
      assert = { quoteOutputMatches(tryExpression) }
    ))
  }
}
