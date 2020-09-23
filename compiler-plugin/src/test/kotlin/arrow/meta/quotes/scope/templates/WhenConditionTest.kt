package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.WhenConditionPlugin
import org.junit.jupiter.api.Test

class WhenConditionTest  {

  companion object {
    val whenConditionExpression = """
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
  fun `Validate when condition scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(WhenConditionPlugin())) },
      code = { whenConditionExpression },
      assert = { quoteOutputMatches(whenConditionExpression) }
    ))
  }
}
