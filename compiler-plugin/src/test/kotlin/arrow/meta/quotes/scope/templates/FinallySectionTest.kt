package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.FinallySectionPlugin
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

// TODO implement convertFinally in Converter to support FINALLY in AST

class FinallySectionTest  {

  private val finallySection = """
                         | //metadebug
                         | 
                         | fun measureTimeMillis(block: () -> Unit): Long {
                         |    val start = System.currentTimeMillis()
                         |    try {
                         |      block()
                         |    } finally {
                         |      return System.currentTimeMillis() - start
                         |    }
                         |  }
                         | """.source

  @Disabled
  @Test
  fun `Validate finally section scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(FinallySectionPlugin())) },
      code = { finallySection },
      assert = { quoteOutputMatches(finallySection) }
    ))
  }
}
