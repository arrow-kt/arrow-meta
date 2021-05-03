package arrow.meta.plugins.liquid

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class LiquidTests {

  @Test
  fun `validation passes at compile and runtime`() {
    passingRefinedExpressionTest(
      expression = "PositiveInt(1).value",
      value = 1
    )
  }

  @Test
  fun `validation fails at compile and runtime`() {
    failedRefinedExpressionTest(
      expression = "PositiveInt(-1).value",
      msg = "-1 should be > 0"
    )
  }


  @Test
  fun `Valid twitter handle passes`() {
    passingRefinedExpressionTest(
      expression = """TwitterHandle("@ok").value""",
      value = "@ok"
    )
  }

  @Test
  fun `Invalid twitter handle 'admin' and fails`() {
    failedRefinedExpressionTest(
      expression = """TwitterHandle("admin").value""",
      msg = "should not contain the word 'admin'"
    )
  }

}

private fun passingRefinedExpressionTest(expression: String, value: Any?, prelude: String = ""): Unit =
  """
      |${imports()}
      |$prelude
      |val z = $expression
      """(
    withPlugin = { "z".source.evalsTo(value) },
    withoutPlugin = { "z".source.evalsTo(value) }
  )

private fun failedRefinedExpressionTest(expression: String, msg: String, prelude: String = ""): Unit =
  """
      |${imports()}
      |$prelude
      |val z = $expression
      """(
    withPlugin = { failsWith { it.contains(msg) } },
    withoutPlugin = { "z".source.evalsTo(RuntimeError(msg)) { RuntimeError(it) } }
  )


private fun imports() =
  """
package test

import arrow.refinement.Refined
import arrow.refinement.ensure
import arrow.refinement.PositiveInt
import arrow.refinement.Even

data class TwitterHandle private constructor(val value: String) {
  companion object : Refined<String, TwitterHandle>(::TwitterHandle, {
    ensure(
      it.startsWith('@') to "${'$'}it should start with @",
      (it.length <= 16) to "${'$'}it should not contain more than 16 characters but it's ${'$'}{it.length} characters long",
      (("twitter" !in it) to "should not contain the word 'twitter'"),
      (("admin" !in it) to "should not contain the word 'admin'")
    )
  })
}

data class NotBlank private constructor(val value: String) {
  companion object
    : Refined<String, NotBlank>(::NotBlank, {
    ensure((it.isNotBlank()) to "expected not blank")
  })
}

data class TwitterHandleNotBlank private constructor(val value: String) {
  companion object : Refined<String, TwitterHandleNotBlank>(::TwitterHandleNotBlank, TwitterHandle, NotBlank)
}


 """

data class RuntimeError(val msg: String?) {
  companion object {
    operator fun invoke(e: Throwable): RuntimeError =
      if (e is ExceptionInInitializerError) RuntimeError(e.exception.message)
      else throw e
  }
}

// TODO update arrow dependencies to latest to test validated support
private operator fun String.invoke(
  withPlugin: AssertSyntax.() -> Assert,
  withoutPlugin: AssertSyntax.() -> Assert
) {
  assertThis(CompilerTest(
    config = { metaDependencies },
    code = { this@invoke.source },
    assert = { withPlugin() }
  ))
  assertThis(CompilerTest(
    config = { listOf(addDependencies(prelude(System.getProperty("CURRENT_VERSION")))) },
    code = { this@invoke.source },
    assert = { withoutPlugin() }
  ))
}

  