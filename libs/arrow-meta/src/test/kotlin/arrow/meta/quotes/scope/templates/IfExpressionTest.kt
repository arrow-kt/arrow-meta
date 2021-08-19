package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.IfExpressionPlugin
import org.junit.jupiter.api.Test

class IfExpressionTest {

  companion object {
    private val ifExpression = """
      | if (2 == 3) {
      |   println("FAKE NEWS")
      | } else {
      |   println("success!")
      | }""".ifExpression()

    private val withoutElse = """
      | if (2 == 3) {
      |   println("FAKE NEWS")
      | }""".ifExpression()

    private val singleLine = """if (2 == 3) println("FAKE NEWS") else println("success")""".ifExpression()

    private fun String.ifExpression(): Code.Source {
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

    val ifExpressions = arrayOf(
      ifExpression,
      withoutElse,
      singleLine
    )
  }

  @Test
  fun `Validate if expression scope properties`() {
    validate(ifExpression)
  }

  @Test
  fun `Do not validate if expression without else scope properties`() {
    validate(withoutElse)
  }

  @Test
  fun `Validate if expression single line scope properties`() {
    validate(singleLine)
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(IfExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }
}
