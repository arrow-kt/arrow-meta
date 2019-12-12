package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.IfExpressionPlugin
import org.junit.Test

class IfExpressionTest  {

  @Test
  fun `Validate if expression scope properties`() {
    validate(
      """
      | if (2 == 3) {
      |   println("FAKE NEWS")
      | } else {
      |   println("success!")
      | }""".ifExpression())
  }

  @Test
  fun `Do not validate if expression without else scope properties`() {
    validate(
      """
      | if (2 == 3) {
      |   println("FAKE NEWS")
      | }""".ifExpression())
  }

  @Test
  fun `Validate if expression single line scope properties`() {
    validate("""if (2 == 3) println("FAKE NEWS") else println("success")""".ifExpression())
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(IfExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }

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
}

class Wrapper {
  fun whatever() {
    if (2 == 3) {
      println("FAKE NEWS")
    }
  }
}
