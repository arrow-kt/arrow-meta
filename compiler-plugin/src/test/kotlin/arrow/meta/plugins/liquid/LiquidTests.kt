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
      |val z = Positive(1)
      """(
      withPlugin = { "z".source.evalsTo(1) },
      withoutPlugin = { "z".source.evalsTo(1) }
    )
  }

  @Test
  fun `validation fails at compile and runtime`() {
    """
      |${imports()}
      |val z = Positive(-1)
      """(
      withPlugin = {
        failsWith {
          it.contains("-1 should be > 0")
        }
      },
      withoutPlugin = {
        "z".source.evalsTo(RuntimeError("-1 should be > 0")) { RuntimeError(it) }
      }
    )
  }

  @Test
  fun `exceptions compose when left predicate fails`() {
    """
      |${imports()}
      |
      |inline val PositiveEven get() = Positive and Even 
      |val z = PositiveEven(-2)
      """(
      withPlugin = {
        failsWith {
          it.contains("-2 should be > 0") && !it.contains("-2 should be an even number")
        }
      },
      withoutPlugin = {
        "z".source.evalsTo(RuntimeError("-2 should be > 0")) { RuntimeError(it) }
      }
    )
  }

}

private fun imports() =
  """
package test

import arrow.Predicate
import arrow.Positive
import arrow.Even

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

  