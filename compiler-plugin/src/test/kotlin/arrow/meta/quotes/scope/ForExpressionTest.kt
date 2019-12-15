package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ForExpressionPlugin
import arrow.meta.quotes.scope.plugins.ReturnExpressionPlugin
import io.kotlintest.specs.AnnotationSpec
class ForExpressionTest : AnnotationSpec() {

  @Test
  fun `Validate for expression scope properties`() {
    validate(""" 
                | //metadebug
                | 
                | fun someFunction() {
                |   for (i in 1..10) {
                |     println(i)
                |   }
                | }
                | """.forExpression())
  }

  @Test
  fun `Validate for expression single line scope properties`() {
    validate("""
                  | fun singleLineFunction() {
                  |     for (i in 1..10) println(i)
                  | }
                  |   """.forExpression())
  }

  @Test
  fun `Validate for expression destructuring declaration scope properties`() {
    validate("""
                  | fun destructuringDeclarationFunction() {
                  |         for ((index, value) in listOf("a", "b", "c").withIndex()) {
                  |              println("index: " + index)
                  |              println("value: " + value)
                  |         }
                  | }
                  |   """.forExpression())
  }

  fun validate(source: Code.Source) {
    assertThis(CompilerTest(
            config = { listOf(addMetaPlugins(ForExpressionPlugin())) },
            code = { source },
            assert = { quoteOutputMatches(source) }
    ))
  }

  private fun String.forExpression(): Code.Source {
    return """
      | //metadebug
      | 
      | class Wrapper {
      |   $this
      | }
      | """.source
  }
}