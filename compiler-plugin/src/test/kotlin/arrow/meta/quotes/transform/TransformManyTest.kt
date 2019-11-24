package arrow.meta.quotes.transform

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.transform.plugins.TransformMetaPlugin
import org.junit.Test

class TransformManyTest {
    
  @Test
  fun `check if the transforms are applied`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        | class ManySimpleCase {
        |   fun printFirst() = println("Foo")
        |   fun printSecond() = println("Bar")
        | }
        """.source
      },
      assert = {
        quoteOutputMatches(
          """
          | @arrow.synthetic private class ManySimpleCase {
          |   fun printSecond() = println("Bar")
          | }
          """.source
        )
      }
    ))
  }
  
  @Test
  fun `check if many plugin creates functions and also clean them`() {
    assertThis(CompilerTest(
     config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
     code = {
      """
        | //metadebug
        |
        | class ManyCustomCase {}
      """.source
      },
      assert = {
        quoteOutputMatches(
          """
          | @arrow.synthetic private class ManyCustomCase {
          |   fun printSecond() = println("Bar")
          | }
          """.source
          )
      }
    ))
  }
  
  @Test
  fun `check if many plugin will apply all replace transformations`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
       """
       | //metadebug
       |
       | class ManyReplace {
       |   fun test() {}
       | }
       """.source
      },
      assert = { quoteOutputMatches(""" @arrow.synthetic private class ManyReplace """.source) }
    ))
  }
  
  @Test
  fun `check if many plugin will apply all remove transformations`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        | class ManyRemove {
        |   fun printFirst() = println("Foo")
        |   fun printSecond() = println("Bar")
        | }
        """.source
      },
      assert = { quoteOutputMatches(""" class ManyRemove """.source) }
    ))
  }
}