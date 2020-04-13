package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Ignore
import org.junit.Test

class GivenTest {

  @Test
  fun `coherent polymorphic identity`() {
    givenTest(
      source = """
        @Given val x = "yes!"
        val result = given<String>()
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `coherent polymorphic identity inference`() {
    givenTest(
      source = """
        @Given val x = "yes!"
        val result: String = given()
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `coherent concrete identity`() {
    givenTest(
      source = """
        @Given val x = "yes!"
        fun id(evidence: @Given String = arrow.given): String =
          evidence
        val result = id()
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `user explicit local override`() {
    givenTest(
      source = """
        @Given val x = "yes!"
        fun id(evidence: @Given String = arrow.given): String =
          evidence
        val result = id("nope!")
      """,
      expected = "result" to "nope!"
    )
  }

  @Test
  fun `value provider`() {
    givenTest(
      source = """
        class X(val value: String)
        @Given val x: X = X("yes!")
        val result = given<X>().value
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `fun provider`() {
    givenTest(
      source = """
        class X(val value: String)
        @Given fun x(): X = X("yes!")
        val result = given<X>().value
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `class provider`() {
    givenTest(
      source = """
        @Given class X {
          val value = "yes!"
        }
        val result = given<X>().value
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `object provider`() {
    givenTest(
      source = """
        @Given object X {
          val value = "yes!"
        }
        val result = given<X>().value
      """,
      expected = "result" to "yes!"
    )
  }

  private val prelude = """
    package test
    import arrow.*
    import arrowx.*
    
    fun <A> given(evidence: @Given A = arrow.given): A =
      evidence
      
    //metadebug
  """.trimIndent()

  private fun givenTest(source: String, expected: Pair<String, Any?>) {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    val codeSnippet = """
       $prelude
       $source
      """
    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf(expected.first.source.evalsTo(expected.second))
      }
    ))
  }
}
