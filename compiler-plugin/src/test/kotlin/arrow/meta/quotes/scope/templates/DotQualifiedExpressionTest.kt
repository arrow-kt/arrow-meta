package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.DotQualifiedExpressionPlugin
import org.junit.Test

class DotQualifiedExpressionTest  {

  companion object {
    private val dotQualifiedExpression =  """
      | val list = listOf("12", "33", "65")
      |     list.flatMap { it.toList() }
      |""".dotQualifiedExpression()

    private val mutableDotQualifiedExpression = """"Shortest".plus("sentence").plus("ever")""".dotQualifiedExpression()

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

    val dotQualifiedExpressions = arrayOf(
      dotQualifiedExpression,
      mutableDotQualifiedExpression
    )
  }

  @Test
  fun `Validate dot qualified expression scope properties`() {
    validate(dotQualifiedExpression)
  }

  @Test
  fun `Validate multiple dot qualified expression scope properties`() {
    validate(mutableDotQualifiedExpression)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(DotQualifiedExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}
