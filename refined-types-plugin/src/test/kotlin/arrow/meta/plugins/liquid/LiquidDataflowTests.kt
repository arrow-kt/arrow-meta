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
        if (x > 0) return 2 else return 3
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
        pre("x less than 10") { x < 10 }
        val z = x + 2
        return z.post("it == x + 2") { r -> r == x + 2 } 
      }
      val result = bar(1)
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `annotated pre-conditions are satisfied in call`() {
    """
      ${imports()}
      
      @Pre(formulae = ["(declare-fun int (Int) Int)\n(declare-fun x () Int)\n(assert (< (int x) 10))\n", "(declare-fun int (Int) Int)\n(declare-fun x () Int)\n(assert (> (int x) 0))\n"])
      fun bar(x: Int): Int =
        x + 2
     
      val result = bar(1)
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `annotated pre-conditions are checked in call `() {
    """
      ${imports()}
      
      @Pre(formulae = ["(declare-fun int (Int) Int)\n(declare-fun x () Int)\n(assert (< (int x) 10))\n", "(declare-fun int (Int) Int)\n(declare-fun x () Int)\n(assert (> (int x) 0))\n"])
      fun bar(x: Int): Int =
        x + 2
     
      val result = bar(30)
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy its pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in call `() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(other: Int): Int {
        pre("other is not zero") { other != 0 }
        return this / other
      }
     
      val result = 1 / 0
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy its pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }


}

private fun imports() =
  """
package test

import arrow.refinement.pre
import arrow.refinement.post
import arrow.refinement.Pre
import arrow.refinement.Post
import arrow.refinement.Law

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

