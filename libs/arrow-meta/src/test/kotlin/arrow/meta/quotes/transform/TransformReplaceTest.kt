package arrow.meta.quotes.transform

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.transform.plugins.TransformMetaPlugin
import org.junit.jupiter.api.Test

class TransformReplaceTest {

  @Test
  fun `should replace function scope to print message`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        |	fun transformReplace() = TODO()
        """.source
      },
      assert = {
        quoteOutputMatches(""" fun transformReplace() = println("Transform Replace") """.source)
      }
    ))
  }

  @Test
  fun `check if extra function is generated`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        | class Foo() {}
        """.source
      },
      assert = {
        quoteOutputMatches(
          """
          | class FooModified {
          |   fun generatedFun() = println("Generated function")
          | }
          """.source
        )
      }
    ))
  }
}
