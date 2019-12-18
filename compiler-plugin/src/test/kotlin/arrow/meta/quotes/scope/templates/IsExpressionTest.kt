package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.IsExpressionPlugin
import org.junit.Test

class IsExpressionTest  {

  @Test
  fun `Validate is expression scope properties`() {
    validate("""
      |if (2 is Int) {
      |  println("2 is a number")
      |} else {
      |  println("2 is not a number")
      |}
      |""".isExpression())
  }

  @Test
  fun `Validate true is expression scope properties`() {
    validate("""
      |if (true) {
      |  println("2 is a number")
      |} else {
      |  println("2 is not a number")
      |}
      |""".isExpression())
  }

  @Test
  fun `Validate as expression scope properties`() {
    validate("""val e = System.currentTimeMillis() as Number""".isExpression())
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
      assert = { compiles }//quoteOutputMatches(source) }
    ))
  }

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
}
