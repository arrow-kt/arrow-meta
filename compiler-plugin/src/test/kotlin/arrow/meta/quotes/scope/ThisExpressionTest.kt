package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.Code
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ThisExpressionPlugin
import io.kotlintest.specs.AnnotationSpec

class ThisExpressionTest : AnnotationSpec() {

  @Test
  fun `Validate this expression with labeled qualifiers scope properties`() {
    validate("""
        | fun Int.foo() {
        |   val a = this@A
        |   val b = this@B
        |   val c1 = this@foo
        | }""".thisExpression())
  }

  @Test
  fun `Validate this expression no labeled target scope properties`() {
    validate("""
      | val funLit2 = { s: String ->
      |  val d1 = this
      | }
      | """.thisExpression())
  }

  @Test
  fun `Validate this expression with reflection target scope properties`() {
    validate("""
      | fun stringToSentence(input: String): String = input + " is in a sentence."
      | val funLit2 = { s: String -> this::stringToSentence }
      | """.thisExpression())
  }

  private fun validate(source: Code.Source) {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(ThisExpressionPlugin())) },
      code = { source },
      assert = { quoteOutputMatches(source) }
    ))
  }

  private fun String.thisExpression(): Code.Source {
    return """
      | //metadebug
      | 
      | class A {
      |   inner class B {
      |     $this
      |   }
      | }
      | """.trimMargin().trim().source
  }
}