package arrow.meta.plugins.liquid

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Disabled
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
  fun `post-conditions are checked, 1`() {
    """
      ${imports()}
      fun bar(x: Int): Int =
        3.post("greater than 0") { it > 0 }
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions are checked, 2`() {
    """
      ${imports()}
      fun bar(x: Int): Int =
        3.post("smaller than 0") { it < 0 }
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions and variables, 1`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        var z = 0
        z = 2
        return z.post("greater than 0") { it > 0 }
      }
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions and variables, 2`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        var z = 2
        z = 0
        return z.post("greater than 0") { it > 0 }
      }
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `invariants in variables`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        var z = 2 invariant { it > 0 }
        z = 0
        return z
      }
      """(
      withPlugin = { failsWith { it.contains("invariants are not satisfied") } },
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
  @Disabled // does not work yet
  fun `when with patterns`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre("x is >= 0") { x >= 0 }
        return (when (x) {
          0 -> 1
          else -> x
        }).post("result is > 0") { it > 0 }
      }
      """(
      withPlugin = { compiles },
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
      
      @Pre(formulae = ["(< (int x) 10)", "(> (int x) 0)"], dependencies = [])
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
      
      @Pre(formulae = ["(< (int x) 10)", "(> (int x) 0)"], dependencies = [])
      fun bar(x: Int): Int =
        x + 2
     
      val result = bar(30)
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy its pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in call, 1`() {
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

  @Test
  fun `ad-hoc laws are checked in call, 2`() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(other: Int): Int {
        pre("other is not zero") { other != 0 }
        return this / other
      }
     
      fun foo() {
        val x = 1 - 2
        val result = 1 / x
      }
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy its pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  @Disabled
  fun `safe get`() {
    """
      ${imports()}
      
      @Law
      fun <A> List<A>.safeGet(ix: Int): A {
        pre("index non-negative") { ix >= 0 }
        pre("index smaller than size") { ix < size }
        return get(ix)
      }
      """(
      withPlugin = { failsWith { it.contains("inconsistent pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }
}

private fun imports() =
  """
package test

import arrow.refinement.invariant
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
