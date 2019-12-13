package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.CatchClausePlugin
import org.junit.Test

class CatchClauseTest {

  private val catchClause = """
                         | //metadebug
                         | 
                         | fun measureTimeMillis(block: () -> Unit): Unit {
                         |    try {
                         |      block()
                         |    } catch (throwable: Throwable) { println(throwable) }
                         |  }
                         | """.source

  @Test
  fun `Validate catch clause scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(CatchClausePlugin())) },
      code = { catchClause },
      assert = { quoteOutputMatches(catchClause) }
    ))
  }
}