package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.CompilerTest.Companion.allOf
import arrow.meta.plugin.testing.CompilerTest.Companion.failsWith
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.patternMatching.phases.analysis.resolveTypesFor
import arrow.meta.plugins.patternMatching.phases.analysis.wildcards
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
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
            bindingTrace.resolveTypesFor { wildcards(it) }
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
  fun `rewrite constructor pattern match expression`() {
    val code =
      """data class Person(val firstName: String, val lastName: String)
         val person = Person("Matt", "Moore")

         fun case(arg: Any): Person = TODO("Deal with later...")

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
        failsWith { it.contains("_: KtCallExpression:") }
      )
    }
  }

}
