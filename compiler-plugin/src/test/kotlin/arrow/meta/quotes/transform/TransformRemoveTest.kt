package arrow.meta.quotes.transform

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.transform.plugins.TransformMetaPlugin
import io.kotlintest.specs.AnnotationSpec

class TransformRemoveTest : AnnotationSpec() {

  @Test
  fun `check if transformRemove function is deleted from AST`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """
          | //metadebug
          |
          | fun transformRemove() {}
          | fun foo() {}
          """.source
      },
      assert = {
        quoteOutputMatches(""" fun foo() {} """.source)
      }
    ))
  }

  @Test
  fun `check if the element of transformRemoveSingleElement function is deleted from AST`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
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
      assert = {
        quoteOutputMatches(""" fun transformRemoveSingleElement() { println("asd") } """.source)
      }
    ))
  }

  @Test
  fun `check if the elements of transformRemoveElements function are deleted from AST`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
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
      assert = {
        quoteOutputMatches(""" fun transformRemoveElements() { } """.source)
      }
    ))
  }
}