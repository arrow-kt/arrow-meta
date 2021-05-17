package arrow.meta.plugins.liquid

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
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
      msg = "admin should start with @, should not contain the word 'admin'"
    )
  }

  @Test
  fun `Local predicate composition fails`() {
    failedRefinedExpressionTest(
      expression = """TwitterHandleNotBlank("admin").value""",
      msg = "admin should start with @, should not contain the word 'admin'"
    )
  }

  @Test
  fun `Local predicate composition succeeds`() {
    passingRefinedExpressionTest(
      expression = """TwitterHandleNotBlank("@ok").value""",
      value = "@ok"
    )
  }

  @Test
  fun `Remote predicate composition fails`() {
    failedRefinedExpressionTest(
      expression = """PositiveIntEven(-1).value""",
      msg = "-1 should be > 0, -1 should be even"
    )
  }

  @Test
  fun `Remote predicate composition succeeds`() {
    passingRefinedExpressionTest(
      expression = """PositiveIntEven(2).value""",
      value = 2
    )
  }

  @Test
  fun `Remote and local predicate composition fails`() {
    failedRefinedExpressionTest(
      expression = """PositiveIntEven18(16).value""",
      msg = "expected 18"
    )
  }

  @Test
  fun `Remote and local predicate composition succeeds`() {
    passingRefinedExpressionTest(
      expression = """PositiveIntEven18(18).value""",
      value = 18
    )
  }

  @Test
  fun `Remote and local predicate composition fails, composition order swapped`() {
    failedRefinedExpressionTest(
      expression = """PositiveIntEven18OrderSwapped(16).value""",
      msg = "expected 18"
    )
  }

  @Test
  fun `Remote and local predicate composition succeeds, composition order swapped `() {
    passingRefinedExpressionTest(
      expression = """PositiveIntEven18OrderSwapped(18).value""",
      value = 18
    )
  }

  @Test
  fun `failure to evaluate dynamic values results in orNull suggestion`() {
    """
      |${imports()}
      |val n = 1
      |val z = PositiveInt(n)
      """(
      withPlugin = { failsWith { """val n = 1 can't be verified at compile time. Use `Predicate.orNull(val n = 1)` for safe access or `Predicate.require(val n = 1)` for explicit unsafe instantiation""" in it } },
      withoutPlugin = { "z".source.evalsTo(1) }
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
import arrow.refinement.numbers.PositiveInt
import arrow.refinement.numbers.Even

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
    ensure((!it.isBlank()) to "expected not blank")
  })
}

data class TwitterHandleNotBlank private constructor(val value: String) {
  companion object : Refined<String, TwitterHandleNotBlank>(::TwitterHandleNotBlank, TwitterHandle, NotBlank)
}

data class PositiveIntEven private constructor(val value: Int) {
  companion object : Refined<Int, PositiveIntEven>(::PositiveIntEven, PositiveInt, Even)
}

data class Eighteen private constructor(val value: Int) {
  companion object
    : Refined<Int, Eighteen>(::Eighteen, {
    ensure((it == 18) to "expected 18")
  })
}

data class PositiveIntEven18 private constructor(val value: Int) {
  companion object : Refined<Int, PositiveIntEven18>(::PositiveIntEven18, PositiveIntEven, Eighteen)
}

data class PositiveIntEven18OrderSwapped private constructor(val value: Int) {
  companion object : Refined<Int, PositiveIntEven18OrderSwapped>(::PositiveIntEven18OrderSwapped, Eighteen, PositiveIntEven)
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
    config = { newMetaDependencies() },
    code = { this@invoke.source },
    assert = { withPlugin() }
  ))
  assertThis(CompilerTest(
    config = { listOf(addDependencies(refinedTypesLib(System.getProperty("CURRENT_VERSION")))) },
    code = { this@invoke.source },
    assert = { withoutPlugin() }
  ))
}

  