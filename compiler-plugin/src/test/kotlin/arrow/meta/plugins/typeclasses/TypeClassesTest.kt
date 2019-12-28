package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class TypeClassesTest {

  //@Test
  fun `simple case`() {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    val codeSnippet = """
       import arrowx.*
      
       //metadebug
        val aaaa = "1".monoidExt().mcombine("2").monoidExt().mcombine("3").monoidExt().mcombine("4")
        val b = "1".mcombine("2").mcombine("3").mcombine("4").mcombine("5")
        val c = String.mempty()
        val d = c.mcombine(b)
      """

    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf("d".source.evalsTo("12345"))
      }
    ))
  }

  @Test
  fun `polymorphic constrain`() {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    val codeSnippet = """
       import arrowx.*
       import arrow.given
      
        
          
      
       //metadebug
       //TODO this is wrong because the returned proof is not applied to monoidExt and the applicative proof gets injected because of the A type arg
       //TODO type susbtitution is not happening correctly in the returned proof
        val result2 = "1".monoidSyntax().combine("2")
        val result3 = "1".combine("2")
        val result = "1".mappend("2")
      """
    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf("result".source.evalsTo("12"))
      }
    ))
  }
}

