package arrow.meta.plugins.patternMatching

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.allOf
import arrow.meta.plugin.testing.CompilerTest.Companion.evalsTo
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
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
  fun `rewrite constructor pattern match expression`() {
    val code =
      """$personSource
         fun case(arg: Any): Person = TODO("Deal with later...")
         
         @Suppress("UNRESOLVED_REFERENCE")
         val result = when (person) {
           case(Person(_, "Moore")) -> "Matched"
           else -> "Not matched"
         }
         """

    code verify {
      allOf(
        "result".source.evalsTo("Matched")
      )
    }
  }

}
