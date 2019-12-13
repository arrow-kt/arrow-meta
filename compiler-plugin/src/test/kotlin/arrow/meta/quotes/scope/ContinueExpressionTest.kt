package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ReturnExpressionPlugin
import org.junit.Test

class ContinueExpressionTest {

  @Test
  fun `Validate continue expression scope properties`() {
    validate("""
        | loop@ for (i in 1..100) {
        |   for (j in 1..100) {
        |     if (j > 30) continue@loop
        |   }
        | } 
        | """.continueExpression())
  }

  @Test
  fun `Validate continue expression no labeled target scope properties`() {
    validate("""
        | for(i in 0 until 100 step 3) {
        |   if (i == 6) continue
        |   if (i == 60) break
        |   println(i)
        | }
        | """.continueExpression())
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ReturnExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }

  private fun String.continueExpression(): Code.Source {
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
