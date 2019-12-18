package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ReturnExpressionPlugin
import org.junit.Test

class ReturnExpressionTest  {

  @Test
  fun `Validate return expression scope properties`() {
    validate("""
        | fun whatTimeIsIt(): Long {
        |   return System.currentTimeMillis()
        | }
        | """.returnExpression())
  }

  @Test
  fun `Validate labeled return expression scope properties`() {
    validate("""
        | fun foo() {
        |   run loop@{
        |     listOf(1, 2, 3, 4, 5).forEach {
        |       if (it == 3) return@loop
        |       print(it)
        |     }
        |   }
        | }
        | """.returnExpression())
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ReturnExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }

  private fun String.returnExpression(): Code.Source {
    return """
      | //metadebug
      | 
      | class Wrapper {
      |   $this
      | }
      | """.source
  }
}
