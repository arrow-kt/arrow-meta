package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhenEntryPlugin
import org.junit.jupiter.api.Test

class WhenEntryTest {

  companion object {
    val whenEntryExpression =
      """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |   fun doMaths(x: Int) {
                         |     when {
                         |       x + 2 == 4 -> println("I can do maths")
                         |       else -> println("I cannot do maths")
                         |     }
                         |   }
                         | }
                         | """.source
  }

  @Test
  fun `Validate when entry scope properties`() {
    assertThis(
      CompilerTest(
        config = { listOf(addMetaPlugins(WhenEntryPlugin())) },
        code = { whenEntryExpression },
        assert = { quoteOutputMatches(whenEntryExpression) }
      )
    )
  }
}
