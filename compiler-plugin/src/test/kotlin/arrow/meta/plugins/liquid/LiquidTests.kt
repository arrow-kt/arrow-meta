package arrow.meta.plugins.liquid

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class LiquidTests {

  @Test
  fun `validation passes at compile and runtime`() {
    """
      |${imports()}
      |val z = PositiveLessThan100(1)
      """(
      withPlugin = { "z".source.evalsTo(1) },
      withoutPlugin = { "z".source.evalsTo(1) }
    )
  }

  @Test
  fun `validation fails at compile and runtime`() {
    """
      |${imports()}
      |val z = PositiveLessThan100(101)
      """(
      withPlugin = {
        failsWith {
          it.contains("Compile time constraint failed: expected 101 to be < 100")
        }
      },
      withoutPlugin = {
        "z".source.evalsTo(RuntimeError("Constraint failed: expected 101 to be < 100")) { RuntimeError(it) }
      }
    )
  }

}

private fun imports() =
  """|package test
        |
        |import arrow.Refinement
        |import arrow.require
        |import arrow.constraint
        |
        |@Refinement
        |inline fun PositiveLessThan100(n: Int): Int {
        |  require(
        |      constraint(n > 0, "expected ${'$'}n to be > 0"),
        |      constraint(n < 100, "expected ${'$'}n to be < 100")
        |  )
        |  return n
        |}
        | """.trimMargin()

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

  