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
        /* TODO:
            We're past type-checking, however I still need to properly search for the right parameter type.
            Right now I'm still just hardcoding a lot to experiment with the concept.
         */
        //"result".source.evalsTo("Matched")
        failsWith { it.contains("_: KtCallExpression:") }
      )
    }
  }

}
