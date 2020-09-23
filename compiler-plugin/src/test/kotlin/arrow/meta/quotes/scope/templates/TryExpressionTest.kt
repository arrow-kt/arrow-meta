package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.TryExpressionPlugin
import org.junit.jupiter.api.Test

class TryExpressionTest  {

  companion object {
    val tryExpression = """
                         | //metadebug
                         | 
                         | fun measureTimeMillis(block: () -> Unit): Unit {
                         |    try {
                         |      block()
                         |    } catch (throwable: Throwable) { println(throwable) }
                         |  }
                         | """.source
  }

  @Test
  fun `Validate try expression scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(TryExpressionPlugin())) },
      code = { tryExpression },
      assert = { quoteOutputMatches(tryExpression) }
    ))
  }
}
