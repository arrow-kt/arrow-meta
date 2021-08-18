package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.IsExpressionPlugin
import org.junit.jupiter.api.Test

class IsExpressionTest {

  companion object {
    private val isExpression = """
      |if (2 is Int) {
      |  println("2 is a number")
      |} else {
      |  println("2 is not a number")
      |}
      |""".isExpression()

    private val trueIsExpression = """
      |if (true) {
      |  println("2 is a number")
      |} else {
      |  println("2 is not a number")
      |}
      |""".isExpression()

    private val asExpression = """val e = System.currentTimeMillis() as Number""".isExpression()

    private fun String.isExpression(): Code.Source {
      return """
      | //metadebug
      | 
      | class Wrapper {
      |   fun whatever() {
      |     $this
      |   }
      | }
      | """.source
    }

    val isExpressions = arrayOf(
      isExpression,
      trueIsExpression,
      asExpression
    )
  }

  @Test
  fun `Validate is expression scope properties`() {
    validate(isExpression)
  }

  @Test
  fun `Validate true is expression scope properties`() {
    validate(trueIsExpression)
  }

  @Test
  fun `Validate as expression scope properties`() {
    validate(asExpression)
  }

  @Test
  fun `Validate is? expression scope properties`() {
    // TODO
  }

  @Test
  fun `Validate as? expression scope properties`() {
    // TODO
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(IsExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}
