package arrow.meta.plugins.patternMatching

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class PatternMatchingTests {
  @Test
  fun `destructuring`() {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowOptics = Dependency("arrow-optics:$arrowVersion")

    /**
     * Since Kotlin doesn't allow for top-level destructuring, we have to wrap in something (I'm using a function here).
     */
    val codeSnippet =
      """|data class Person(val firstName: String, val lastName: String)
         |
         |val person: Person = Person("Matt", "Moore")
         |
         |fun destructure(): List<String> {
         |  val (fName, lName) = person
         |  return listOf(fName, lName)
         |}
         |
         |val firstName = destructure()[0]
         |val lastName = destructure()[1]
         |"""

    assertThis(CompilerTest(
      config = { metaDependencies + addDependencies(arrowOptics) },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf(
          "firstName".source.evalsTo("Matt"),
          "lastName".source.evalsTo("Moore")
        )
      }
    ))
  }

}
