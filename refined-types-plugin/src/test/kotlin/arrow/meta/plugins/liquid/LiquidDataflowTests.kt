package arrow.meta.plugins.liquid

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Test

class LiquidDataflowTests {

  @Test
  fun `pre and post are resolved in the classpath`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre("x is 42") { x == 42 }
        val z = x + 2
        return z.post("returns 44") { it == 44 }
      }
      val result = bar(1)
      """(
      withPlugin = { "result".source.evalsTo(44) },
      withoutPlugin = { "result".source.evalsTo(44) }
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

