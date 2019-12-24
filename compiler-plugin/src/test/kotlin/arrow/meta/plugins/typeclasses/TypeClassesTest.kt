package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class TypeClassesTest {

  @Test
  fun `simple case`() {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    val codeSnippet = """
       package test
       
       import arrowx.*
      
       //metadebug
        
        val a2: Id<Int> = Id.just2(1)
        val a: Id<Int> = Id.just(1)
        
        fun f(): Int {
          val x : Id<Int> = a
          return x.value
        }
      """

    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf("f()".source.evalsTo(2))
      }
    ))
  }
}

