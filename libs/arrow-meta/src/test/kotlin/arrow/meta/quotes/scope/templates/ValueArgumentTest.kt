package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ValueArgumentPlugin
import org.junit.jupiter.api.Test

class ValueArgumentTest {

  @Test
  fun `Validate value argument scope properties`() {
    validate("""addNumbers(x = 3, y = 4)""".valueArgument())
  }

  @Test
  fun `Validate value argument scope properties no argumentNames`() {
    validate("""addNumbers(3, 4)""".valueArgument())
  }

  private fun validate(source: Code.Source) {
    assertThis(
      CompilerTest(
        config = { listOf(addMetaPlugins(ValueArgumentPlugin())) },
        code = { source },
        assert = { quoteOutputMatches(source) }
      )
    )
  }

  private fun String.valueArgument(): Code.Source {
    return """
      | //metadebug
      | 
      | class Wrapper {
      |   init {
      |    $this
      |   }
      |   fun addNumbers(x: Int, y: Int): Int = x + y
      |  }
      | """.source
  }
}
