package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TypeClassesTest {

    //@Test
    fun `simple case`() {
        val codeSnippet = """
       import arrowx.*
       import arrow.*
      
       //metadebug
        val aaaa = "1".monoidExt().mcombine("2").monoidExt().mcombine("3").monoidExt().mcombine("4")
        val b = "1".mcombine("2").mcombine("3").mcombine("4").mcombine("5")
        val c = String.mempty()
        val d = c.mcombine(b)
      """

        assertThis(CompilerTest(
            config = {
                metaDependencies
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
    val codeSnippet = """
       import arrow.*
       import arrowx.*
       
       fun <A: @Given Semigroup<A>> A.mappend(b: A): A =
          this@mappend.combine(b)

       //metadebug
        val result1 = String.empty()
        val result2 = "1".combine("1")
        val result3 = "2".mappend("2")
        val result = result1.combine(result2).combine(result3)
      """
    assertThis(CompilerTest(
      config = {
        metaDependencies
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf(
          "result1".source.evalsTo(""),
          "result2".source.evalsTo("11"),
          "result3".source.evalsTo("22"),
          "result".source.evalsTo("1122")
        )
      }
    ))
  }

}

