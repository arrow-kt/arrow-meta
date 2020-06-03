package arrow.meta.plugins.patternMatching

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.allOf
import arrow.meta.plugin.testing.CompilerTest.Companion.failsWith
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class PatternMatchingTests {
  private infix fun String.verify(assertion: (CompilerTest.Companion) -> Assert) = also {
    assertThis(CompilerTest(
//      config = { listOf(CompilerTest.addMetaPlugins(PatternMatchingPlugin())) },
      config = { metaDependencies },
      code = { it.source }, assert = assertion
    ))
  }

//  open class PatternMatchingPlugin : Meta {
//    override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
//      patternMatching
//    )
//  }

  @Test
  fun `rewrite constructor pattern match expression`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
         val person = Person("Matt", "Moore")

         fun case(arg: Any): Person = TODO("Deal with later...")

         @Suppress("UNRESOLVED_REFERENCE")

         val result = when (person) {
           case(Person(_, "Moore")) -> "Matched"
           else -> "Not matched"
         }
         """

    code verify {
      allOf(
        /* TODO:
            We're past the type-checking phase, however I still need to properly search for the right parameter type.
            Right now I'm still just hardcoding a lot to experiment with the concept.
            This does demonstrate getting past the type-checking phase, and now we'll have a codegen failure.
            In codegen we'll need to transform _ to the property that goes along with the constructor for the class.
         */
        //"result".source.evalsTo("Matched")
        failsWith { it.contains("Failed to generate expression: KtNameReferenceExpression") }
      )
    }
  }

}
