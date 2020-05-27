package arrow.meta.plugins.patternMatching

import arrow.meta.plugin.testing.*
import arrow.meta.plugin.testing.CompilerTest.Companion.evalsTo
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.CompilerTest.Companion.allOf
import org.junit.Test

class PatternMatchingTests {
  private infix fun String.verify(assertion: (CompilerTest.Companion) -> Assert) = also {
    assertThis(CompilerTest(
      config = { CompilerTest.metaDependencies },
      code = { it.source }, assert = assertion
    ))
  }

  private val personSource =
    """|data class Person(val firstName: String, val lastName: String)
       |val person = Person("Matt", "Moore")
       |"""

  @Test
  fun `destructuring`() {
    val code =
      """$personSource
         |
         |fun destructure(): List<String> {
         |  val (fName, lName) = person
         |  return listOf(fName, lName)
         |}
         |
         |val firstName = destructure()[0]
         |val lastName = destructure()[1]
         |"""

    code verify {
      allOf(
        "firstName".source.evalsTo("Matt"),
        "lastName".source.evalsTo("Moore")
      )
    }
  }

  @Test
  fun `rewrite constructor pattern match expression`() {
    val code =
      """|$personSource
         |
         |fun destructure(): String {
         |  val Person(_, lastName) = person
         |  return lastName
         |}
         |"""

    code verify {
      allOf(
        "destructure()".source.evalsTo("Moore")
      )
    }
  }

}
