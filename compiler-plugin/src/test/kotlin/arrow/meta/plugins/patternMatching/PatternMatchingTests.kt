package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.allOf
import arrow.meta.plugin.testing.CompilerTest.Companion.evalsTo
import arrow.meta.plugin.testing.CompilerTest.Companion.failsWith
import arrow.meta.plugin.testing.CompilerTest.Companion.source
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.patternMatching.phases.analysis.resolvePatternExpression
import arrow.meta.plugins.patternMatching.phases.analysis.wildcards
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference
import org.junit.Test

open class PatternMatchingPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    patternMatchingPlugin
  )
}

val Meta.patternMatchingPlugin: CliPlugin
  get() =
    "Pattern Matching Plugin" {
      meta(
        enableIr(),
        suppressDiagnostic { ctx.suppressUnresolvedReference(it) },
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            bindingTrace.resolvePatternExpression { it.wildcards }
            null
          }
        ),
        irDump()
      )
    }

class PatternMatchingTests {
  private infix fun String.verify(assertion: (CompilerTest.Companion) -> Assert) = also {
    assertThis(CompilerTest(
      config = { listOf(CompilerTest.addMetaPlugins(PatternMatchingPlugin())) },
      code = { it.source }, assert = assertion
    ))
  }

  @Test
  fun `without case pattern match expression`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
         val person = Person("Matt", "Moore")

         fun case(arg: Any?): Any? = arg

         val result = when (person) {
           case(Person("Matt", "Moore")) -> "Matched"
           else -> "Not matched"
         }
         """

    code verify {
      allOf(
        "result".source.evalsTo("Matched")
      )
    }
  }

  @Test
  fun `with case pattern match expression`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
         val person = Person("Matt", "Moore")

         fun case(arg: Any): Any = arg

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

  @Test
  fun `with case pattern second param match expression`() {
    val code =
        """data class Person(val firstName: String, val lastName: String)
     val person = Person("Matt", "Moore")

     fun case(arg: Any): Any = arg

     val result = when (person) {
       case(Person("Matt", _)) -> "Matched"
       else -> "Not matched"
     }
     """

    code verify {
        allOf(
            "result".source.evalsTo("Matched")
        )
    }
  }

  @Test
  fun `with case pattern captured param results in value`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
     val person = Person("Matt", "Moore")

     fun case(arg: Any): Any = arg

     val result = when (person) {
       case(Person(capturedFirstName, _)) -> capturedFirstName
       else -> "Not matched"
     }
     """

    code verify {
      allOf(
        "result".source.evalsTo("Matt")
      )
    }
  }

  @Test
  fun `with case pattern captured second param results in value`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
     val person = Person("Matt", "Moore")

     fun case(arg: Any): Any = arg

     val result = when (person) {
       case(Person(_, capturedSecondName)) -> capturedSecondName
       else -> "Not matched"
     }
     """

    code verify {
      allOf(
        "result".source.evalsTo("Moore")
      )
    }
  }

  @Test
  fun `with case pattern both captured params result in value`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
     val person = Person("Matt", "Moore")

     fun case(arg: Any): Any = arg

     val result = when (person) {
       case(Person(capturedFirstName, capturedSecondName)) -> capturedFirstName + capturedSecondName
       else -> "Not matched"
     }
     """

    code verify {
      allOf(
//        "result".source.evalsTo("MattMoore")
        // TODO value argument types seem to be not patched when we resolve identifier
        failsWith { it.contains("Value argument in function call is mapped with error") }
      )
    }
  }
}
