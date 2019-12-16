package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.DotQualifiedExpressionPlugin
import org.junit.Test

class DotQualifiedExpressionTest  {

  @Test
  fun `Validate dot qualified expression scope properties`() {
    validate(
      """
      | val list = listOf("12", "33", "65")
      |     list.flatMap { it.toList() }""".dotQualifiedExpression()
    )
  }

  @Test
  fun `Validate multiple dot qualified expression scope properties`() {
    validate(""""Shortest".plus("sentence").plus("ever")""".dotQualifiedExpression()
    )
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(DotQualifiedExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }

  private fun String.dotQualifiedExpression(): Code.Source {
    return """
      | //metadebug
      | 
      | class Wrapper {
      |   fun whatever() {
      |    $this
      |   }
      |  }
      | """.source
  }
}
