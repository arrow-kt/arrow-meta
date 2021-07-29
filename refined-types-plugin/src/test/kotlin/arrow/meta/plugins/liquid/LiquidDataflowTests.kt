package arrow.meta.plugins.liquid

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Test

class LiquidDataflowTests {

  @Test
  fun `inconsistent preconditions`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre("x is 42") { x == 42 }
        pre("x is also 43") { x == 43 }
        val z = x + 2
        return z.post("returns 44") { it == x + 2 }
      }
      val result = bar(1)
      """(
      withPlugin = { failsWith { it.contains("inconsistent pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `unreachable code`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre("x is > 0") { x > 0 }
        if (true) return 2 else return 3
      }
      """(
      withPlugin = { failsWith { it.contains("unreachable code due to conflicting conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are not satisfied in call`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre("x is 42") { x == 42 }
        val z = x + 2
        return z
      }
      val result = bar(1)
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy its pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are satisfied in call`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre("x greater than 0") { x > 0 }
        val z = x + 2
        return z
      }
      val result = bar(1)
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }
}

private fun imports() =
  """
package test

import arrow.refinement.pre
import arrow.refinement.post

 """

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

