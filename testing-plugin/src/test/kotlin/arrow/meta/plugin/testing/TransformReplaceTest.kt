package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.plugins.MetaPlugin
import org.junit.Test

class TransformReplaceTest {
	
  @Test
  fun `should replace function scope to print message`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(MetaPlugin()) },
      code = {
        """
	  | //metadebug
	  |
	  |	fun transformReplace() = TODO()
	""".source
      },
      assert = { allOf(quoteOutputMatches(""" @arrow.synthetic fun transformReplace() = println("Transform Replace") """.source)) }
    ))
  }
	
  @Test
  fun `check if extra function is generated`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(MetaPlugin()) },
	code = {
	  """
	    | //metadebug
	    |
	    | class Foo() {}
	  """.source
	},
	assert = { allOf(quoteOutputMatches(
	  """
	    | @arrow.synthetic class FooModified {
	    |   fun generatedFun() = println("Generated function")
	    | }
	  """.source
	)) }
    ))
  }
}
