package arrow.meta.quotes.scope.templates

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ObjectDeclarationPlugin
import org.junit.jupiter.api.Test

class ObjectDeclarationTest {

  companion object {
    val objectDeclarationExpression =
      """
                         | //metadebug
                         | 
                         | @Deprecated("Test") object X {
                         |   fun x() { println("Test") }
                         |   fun x2() { println("Test2") }
                         | }""".source
  }

  @Test
  fun `Validate object declaration scope properties`() {
    assertThis(
      CompilerTest(
        config = { metaDependencies + addMetaPlugins(ObjectDeclarationPlugin()) },
        code = { objectDeclarationExpression },
        assert = { quoteOutputMatches(objectDeclarationExpression) }
      )
    )
  }
}
