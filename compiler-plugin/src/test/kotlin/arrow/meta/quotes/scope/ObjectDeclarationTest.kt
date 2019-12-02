package arrow.meta.quotes.scope

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.scope.plugins.ObjectDeclarationPlugin
import org.junit.Test

class ObjectDeclarationTest {
  
  private val `object` = """
                         | //metadebug
                         | 
                         | @Deprecated("Test") object Test {
                         |   fun test() { println("Test") }
                         |   fun test2() { println("Test2") }
                         | }""".source
  
  @Test
  fun `Validate object declaration scope properties`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(ObjectDeclarationPlugin()) },
      code = { `object` },
      assert = { quoteOutputMatches(`object`) }
    ))
  }
}