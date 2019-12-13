package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.DestructuringDeclarationPlugin
import org.junit.Test

class DestructuringDeclarationTest {

  private val destructuringDeclaration = """
                         | //metadebug
                         | 
                         | class Wrapper {
                         |    data class Test(val x: String = "X", val y: Int = 1)
                         |    
                         |   fun whatever() {
                         |     val (x, y) = Test()
                         |   } 
                         | }
                         | """.source

  @Test
  fun `Validate destructuring declaration scope properties`() {
    assertThis(CompilerTest(
      config = { listOf(addMetaPlugins(DestructuringDeclarationPlugin())) },
      code = { destructuringDeclaration },
      assert = { quoteOutputMatches(destructuringDeclaration) }
    ))
  }
}