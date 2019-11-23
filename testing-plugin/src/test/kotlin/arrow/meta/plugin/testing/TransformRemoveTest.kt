package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.plugins.MetaPlugin
import org.junit.Test

class TransformRemoveTest {
  
  @Test
  fun `check if transformRemove function is deleted from AST`() {
      assertThis(CompilerTest(
        config = { metaDependencies + addMetaPlugins(MetaPlugin()) },
        code = {
          """
          | //metadebug
          |
          | fun transformRemove() {}
          | fun foo() {}
          """.source
        },
        assert = { allOf(quoteOutputMatches(""" fun foo() {} """.source)) }
      ))
  }
  
  @Test
  fun `check if the element of transformRemoveSingleElement function is deleted from AST`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(MetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        | fun transformRemoveSingleElement() {
        |   println("")
        |   println("asd")
        | }
        """.source
      },
      assert = { allOf(quoteOutputMatches(
          """ fun transformRemoveSingleElement() { println("asd") } """.source)
      )}
    ))
  }
  
  @Test
  fun `check if the elements of transformRemoveElements function are deleted from AST`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(MetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        | fun transformRemoveElements() {
        |   println("")
        |   println("asd")
        | }
        """.source
      },
      assert = { allOf(quoteOutputMatches(
          """ fun transformRemoveElements() { } """.source)
      )}
    ))
  }
}