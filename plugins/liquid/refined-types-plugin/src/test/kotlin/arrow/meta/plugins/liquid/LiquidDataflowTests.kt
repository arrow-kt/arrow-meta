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
  @Disabled
  fun `bad predicate`() {
    """
      ${imports()}
      fun bar(): Int {
        pre( "a" == "b" ) { "wrong" }
        return 1
      }
      """(
      withPlugin = { failsWith { it.contains("could not parse this predicate") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `inconsistent preconditions`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre( x == 42 ) { "x is 42" }
        pre( x == 43 ) { "x is also 43" }
        val z = x + 2
        return z.post({ it == x + 2 }) { "returns 44" }
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
        3.post({ it > 0 }) { "greater than 0" }
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
        3.post({ it < 0 }) { "smaller than 0" }
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `scopes work well`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        val x = 0
        { val x = 2 }
        return x.post({ r -> r > 0 }) { "greater than 0" } 
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
        var z = 2.invariant({ it > 0 }) { "invariant it > 0" }
        z = 0
        return z
      }
      """(
      withPlugin = { failsWith { it.contains("invariants are not satisfied") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `var only knows the invariant, 1`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        var z = 2
        z = 3
        return z.post({ it > 0 }) { "greater than 0" }
      }
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `var only knows the invariant, 2`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        var z = 2.invariant({ it > 0 }) { "invariant it > 0" }
        z = 3
        return z.post({ it >= 0 }) { "greater or equal to 0" }
      }
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `unreachable code`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre( x > 0 ) { "x is > 0" }
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
        pre( x >= 0 ) { "x is >= 0" }
        return (when (x) {
          0 -> 1
          else -> x
        }).post({ it > 0 }) { "result is > 0" }
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
        pre( x == 42 ) { "x is 42" }
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
        pre( x > 0 ) { "x greater than 0" }
        pre( x < 10 ) { "x less than 10" }
        val z = x + 2
        return z.post({ r -> r == x + 2 }) { "it == x + 2" } 
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
      
      @Pre(messages = ["(< (int x) 10)", "(> (int x) 0)"], formulae = ["(< (int x) 10)", "(> (int x) 0)"], dependencies = [])
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
      
      @Pre(messages = ["(< (int x) 10)", "(> (int x) 0)"], formulae = ["(< (int x) 10)", "(> (int x) 0)"], dependencies = [])
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
        pre( other != 0 ) { "other is not zero" }
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
        pre( other != 0 ) { "other is not zero" }
        return this / other
      }
     
      fun foo() {
        val x = 1 - 2
        val result = 1 / x
      }
      """(
      withPlugin = { compiles },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `safe get`() {
    """
      ${imports()}
      
      @Law
      fun <A> List<A>.safeGet(index: Int): A {
        pre( index >= 0 ) { "index non-negative" }
        pre( index < size ) { "index smaller than size" }
        return get(index)
      }
      
      @Law
      fun <A> emptyListIsEmpty(): List<A> =
        emptyList<A>().post({ it.size == 0 }) { "is empty" }
       
      val wrong: String = emptyList<String>().get(0)
      """(
      withPlugin = { failsWith { it.contains("call to `get(0)` fails to satisfy its pre-conditions") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `isEmpty is size == 0`() {
    """
      ${imports()}
      
      @Law
      fun <A> List<A>.safeGet(index: Int): A {
        pre( index >= 0 ) { "index non-negative" }
        pre( index < size ) { "index smaller than size" }
        return get(index)
      }
      
      @Law
      fun <A> List<A>.refinedIsEmpty(): Boolean =
        isEmpty().post({ it == (size <= 0) }) { "equivalent to size 0" }
       
      fun ok(x: List<String>): String {
        pre( !x.isEmpty() ) { "non-empty" }
        return x.get(0)
      }
      """(
      withPlugin = { compiles },
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
