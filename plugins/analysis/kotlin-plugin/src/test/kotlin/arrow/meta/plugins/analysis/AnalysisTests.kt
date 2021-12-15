package arrow.meta.plugins.analysis

import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.AssertSyntax
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugins.newMetaDependencies
import org.junit.jupiter.api.Test

class AnalysisTests {

  @Test
  fun `bad predicate, could not parse predicate`() {
    """
      ${imports()}
      fun bar(): Int {
        pre( "a" == "b" ) { "wrong" }
        return 1
      }
      """(
      withPlugin = { failsWith { it.contains("not parse predicate") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `bad predicate, refers to local variable`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre(x > 0) { "ok" }
        val z = 2
        return 1.post({ z > 0 }) { "wrong" }
      }
      """(
      withPlugin = { failsWith { it.contains("unexpected reference") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `boolean variable used as predicate`() {
    """
      ${imports()}
      fun negate(x: Boolean): Boolean {
        pre(x) { "x is true" }
        return (!x).post({ !it }) { "returns false" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
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
      withPlugin = { compilesNoUnreachable },
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
      withPlugin = {
        failsWith {
          it.contains("declaration `bar` fails to satisfy the post-condition: ${'$'}result < 0")
        }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions are on if`() {
    """
      ${imports()}
      fun bar(x: Int): Int =
        (if (x > 0) 1 else -1).post({ it > 0 }) { "greater than 0" }
      """(
      withPlugin = {
        failsWith {
          it.contains("declaration `bar` fails to satisfy the post-condition: ${'$'}result > 0") &&
            it.contains("in branch: !(x > 0)")
        }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `checks special functions, 1`() {
    """
      ${imports()}
      fun bar(x: Int): Int =
        0.let { it + 1 }.post({ it > 0 }) { "greater than 0" }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `checks special functions, 2`() {
    """
      ${imports()}
      fun bar(x: Int): Int =
        1.run { this - 1 }.post({ it > 0 }) { "greater than 0" }
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
      withPlugin = {
        failsWith {
          it.contains("declaration `bar` fails to satisfy the post-condition: ${'$'}result > 0")
        }
      },
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
      withPlugin = { failsWith { it.contains("invariants are not satisfied in `z = 0`") } },
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
      withPlugin = {
        failsWith {
          it.contains("declaration `bar` fails to satisfy the post-condition: ${'$'}result > 0")
        }
      },
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
      withPlugin = { compilesNoUnreachable },
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
      withPlugin = {
        compilesWith { it.contains("unreachable code due to conflicting conditions") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
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
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `when with val`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre( x >= 0 ) { "x is >= 0" }
        return (when (val y = x + 1) {
          else -> y
        }).post({ it > 0 }) { "result is > 0" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `when, absolute value`() {
    """
      ${imports()}
      fun absoluteValue(n: Int): Int = when {
        n >= 0 -> n
        else   -> -n
      }.post({ it >= 0 }) { "result >= 0" }
      """(
      withPlugin = { compilesNoUnreachable },
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
      withPlugin = {
        failsWith { it.contains("pre-condition `x is 42` is not satisfied in `bar(1)`") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are not satisfied in call, but trust me, 1`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre( x == 42 ) { "x is 42" }
        val z = x + 2
        return z
      }
      val result = unsafeCall(bar(1))
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are not satisfied in call, but trust me, 2`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre( x == 42 ) { "x is 42" }
        val z = x + 2
        return z
      }
      val result = unsafeBlock { bar(1) }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are not satisfied in call, but trust me, 3`() {
    """
      ${imports()}
      fun bar(x: Int): Int {
        pre( x == 42 ) { "x is 42" }
        val z = x + 2
        return z
      }
      val result = unsafeBlock { bar(1) + 1 }
      """(
      withPlugin = { compilesNoUnreachable },
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
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are not satisfied in loop, while`() {
    """
      ${imports()}
      @Pre(messages = ["(< (int x) 0)"], formulae = ["(< (int x) 0)"], dependencies = [])
      fun bar(x: Int): Int =
        x + 2
        
      fun loopy1(t: Int): Int {
        while (t > 0) {
          bar(1)
        }
        return 2
      }
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `(< (int x) 0)` is not satisfied in `bar(1)") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions are not satisfied in loop, for`() {
    """
      ${imports()}
      @Pre(messages = ["(< (int x) 0)"], formulae = ["(< (int x) 0)"], dependencies = [])
      fun bar(x: Int): Int =
        x + 2
        
      fun loopy1(t: List<Int>): Int {
        for (elt in t) {
          bar(1)
        }
        return 2
      }
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `(< (int x) 0)` is not satisfied in `bar(1)`") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `annotated pre-conditions are satisfied in call`() {
    """
      ${imports()}
      
      @Pre(messages = ["x less than 10", "x less than 0"], formulae = ["(< (int x) 10)", "(> (int x) 0)"], dependencies = [])
      fun bar(x: Int): Int =
        x + 2
     
      val result = bar(1)
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `annotated pre-conditions are checked in call `() {
    """
      ${imports()}
      
      @Pre(messages = ["x less than 10", "x less than 0"], formulae = ["(< (int x) 10)", "(> (int x) 0)"], dependencies = [])
      fun bar(x: Int): Int =
        x + 2
     
      val result = bar(30)
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `x less than 10` is not satisfied in `bar(30)`") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `pre-conditions for subjects`() {
    """
      ${imports()}
      @Pre(messages = ["not zero divisor"], formulae = ["(not (= (int other) 0))"], dependencies = [])
      @Subject(fqName = "kotlin.Int.div")
      fun Int.divLaw(other: Int) = this / other
        
      val x: Int = 1 / 0
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `not zero divisor` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions for subjects`() {
    """
      ${imports()}
      import kotlin.Result.Companion.success
      
      @Post(messages = ["create a success"], formulae = ["true"], dependencies = ["kotlin.Result.isSuccess"])
      @Subject(fqName = "kotlin.Result.Companion.success")
      fun <T> Result.Companion.successLaw(x: T): Result<T> = success(x)
        
      val x: Result<Int> = success(3)
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions for subjects, subtype`() {
    """
      ${imports()}
      @Post(messages = ["example"], formulae = ["true"], dependencies = [])
      @Subject(fqName = "kotlin.collections.minus")
      fun <E> Collection<E>.minusLaw(element: E) = minus(element)
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `post-conditions for subjects, function`() {
    """
      ${imports()}
      @Pre(messages = ["not empty"], formulae = ["(>= (int (field kotlin.collections.List.size this)) 1)"], dependencies = ["kotlin.collections.List.size"])
      @Subject(fqName = "kotlin.collections.first")
      fun <E> List<E>.firstLaw(predicate: (x: E) -> Boolean) = first(predicate)
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in call, 1`() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(theOtherNumber: Int): Int {
        pre( theOtherNumber != 0 ) { "other is not zero" }
        return this / theOtherNumber
      }
     
      val result = 1 / 0
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `other is not zero` is not satisfied in `1 / 0`") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in call, 2`() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(theOtherNumber: Int): Int {
        pre( theOtherNumber != 0 ) { "other is not zero" }
        return this / theOtherNumber
      }
     
      fun foo() {
        val x = 1 - 2
        val result = 1 / x
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in call, 3`() {
    """
      ${imports()}
      
      @Laws
      object IntLaws {
        @Law
        fun Int.safeDiv(theOtherNumber: Int): Int {
          pre( theOtherNumber != 0 ) { "other is not zero" }
          return this / theOtherNumber
        }
      }
     
      val result = 1 / 0
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `other is not zero` is not satisfied in `1 / 0`") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in call, 4`() {
    """
      ${imports()}
      
      @Laws
      object IntLaws {
        @Law
        fun Int.safeDiv(theOtherNumber: Int): Int {
          pre( theOtherNumber != 0 ) { "other is not zero" }
          return this / theOtherNumber
        }
      }
     
      val result = 1 / 2
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in lambda, 1`() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(theOtherNumber: Int): Int {
        pre( theOtherNumber != 0 ) { "other is not zero" }
        return this / theOtherNumber
      }
     
      val result = listOf(1, 0).map { n -> 1 / n }
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `other is not zero` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws are checked in lambda, 2`() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(theOtherNumber: Int): Int {
        pre( theOtherNumber != 0 ) { "other is not zero" }
        return this / theOtherNumber
      }
     
      val result = listOf(1, 0).map { 1 / it }
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `other is not zero` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws for constructors, 1`() {
    """
      ${imports()}
      ${arrayListLaws()}
     
      val result = ArrayList<Int>(-1)
      """(
      withPlugin = {
        failsWith {
          it.contains("pre-condition `initial capacity should be non-negative` is not satisfied")
        }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws for constructors, 2`() {
    """
      ${imports()}
      ${arrayListLaws()}
     
      val result = ArrayList<Int>(1)
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `ad-hoc laws for constructors, 3`() {
    """
      ${imports()}
      
      import kotlin.collections.ArrayList
      
      @Pre(messages = ["initial capacity should be non-negative"], formulae = ["(>= (int initialCapacity) 0)"], dependencies = [])
      @Subject(fqName = "kotlin.collections.ArrayList.<init>")
      fun <A> ArrayListConstruction(initialCapacity: Int): ArrayList<A> {
        pre( initialCapacity >= 0 ) { "initial capacity should be non-negative" }
        return ArrayList(initialCapacity)
      }
     
      val result = ArrayList<Int>(-1)
      """(
      withPlugin = {
        failsWith {
          it.contains("pre-condition `initial capacity should be non-negative` is not satisfied")
        }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `incorrect ad-hoc law`() {
    """
      ${imports()}
      
      @Law
      fun Int.safeDiv(theOtherNumber: Int): Int {
        pre( theOtherNumber != 0 ) { "other is not zero" }
        return theOtherNumber / this
      }
      """(
      withPlugin = { failsWith { it.contains("must use the arguments in order") } },
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
      withPlugin = {
        failsWith {
          it.contains("pre-condition `index smaller than size` is not satisfied in `get(0)`")
        }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `safe indexing`() {
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
       
      val wrong: String = emptyList<String>()[0]
      """(
      withPlugin = {
        failsWith {
          it.contains(
            "pre-condition `index smaller than size` is not satisfied in `emptyList<String>()[0]`"
          )
        }
      },
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
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `isEmpty is size == 0, on map`() {
    """
      ${imports()}
      
      @Law
      inline fun <E> List<E>.getLaw(index: Int): E {
        pre(index >= 0 && index < size) { "index within bounds" }
        return get(index)
      }
      
      @Law
      inline fun <E> Collection<E>.isEmptyLaw(): Boolean =
        isEmpty().post({ it == (size <= 0) }) { "empty when size is 0" }
      
      data class Order(val entries: List<Entry>)
      data class Entry(val id: String, val amount: Int)
      
      fun Order.containsSingleValue() =
        if (entries.isEmpty()) false
        else entries.all { entry -> entry.id == entries[0].id }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, predicate with null, 1`() {
    """
      ${imports()}
      fun nully1a(x: Int?): Int? {
        val y = x?.let { 1 }
        return y.post({ 
          if (x == null) { it == null } 
          else { (it != null) && (it > 0) } 
        }) { "greater than 0" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, predicate with null, 2, if`() {
    """
      ${imports()}
      fun nully1b(x: Int?): Int? {
        val y = x?.let { 1 }
        return y.post({ 
          if (x == null) { it == null }
          else { (it != null) && (it < 0) }
        }) { "smaller than 0" }
      }
      """(
      withPlugin = { failsWith { it.contains("`nully1b` fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, predicate with null, 2, when`() {
    """
      ${imports()}
      fun nully1b(x: Int?): Int? {
        val y = x?.let { 1 }
        return y.post({ 
          when {
            x == null -> it == null
            else -> (it != null) && (it < 0)
          }
        }) { "smaller than 0" }
      }
      """(
      withPlugin = { failsWith { it.contains("`nully1b` fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, null check in pre`() {
    """
      ${imports()}
      fun nully1b(x: Int?): Int? {
        pre((x == null) || (x > 0)) { "x is null or positive" }
        val y = x?.let { it + 1 }
        return y.post({ (it == null) || (it > 1) }) { "greater than 1" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, branches are followed on null check`() {
    """
      ${imports()}
      fun nully2(x: Int?): Int {
        val y = if (x == null) 1 else 2
        return y.post({ it > 0 }) { "greater than 0" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, Elvis operator`() {
    """
      ${imports()}
      fun nully3(x: Int?): Int {
        val y = x ?: 1
        return y.post({ it > 0 }) { "greater than 0" }
      }
      """(
      withPlugin = { failsWith { it.contains("`nully3` fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `nullability, is implies non-nullability`() {
    """
      ${imports()}
      fun nully4(x: Int?): Int {
        val y = if (x is Int) 1 else 2
        return y.post({ it > 0 }) { "greater than 0" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `try, 1`() {
    """
      ${imports()}
      fun try1(x: Int): Int {
        val y = try { 1 } catch (e: Exception) { 2 }
        return y.post({ it > 0 }) { "greater than 0" }
      }
    """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `try, 2`() {
    """
      ${imports()}
      fun try2(x: Int): Int {
        val y = try { 1 } catch (e: Exception) { 0 }
        return y.post({ it > 0 }) { "greater than 0" }
      }
      """(
      withPlugin = { failsWith { it.contains("`try2` fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `subtyping, pre- and post-conditions are checked in overridden functions`() {
    """
      ${imports()}
      open class A() {
        open fun f(): Int = 2.post({ it > 0 }) { "greater than 0" }
      }
      
      class B(): A() {
        override fun f(): Int = 0
      }
      """(
      withPlugin = { failsWith { it.contains("`f` fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `subtyping, Liskov Substitution Principle, 1`() {
    """
      ${imports()}
      open class A() {
        open fun f(): Int = 2.post({ it > 0 }) { "greater than 0" }
      }
      
      class B(): A() {
        override fun f(): Int = 1.post({ it >= 0 }) { "non-negative" }
      }
      """(
      withPlugin = {
        failsWith {
          it.contains("post-condition `greater than 0` from overridden member is not satisfied")
        }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `subtyping, Liskov Substitution Principle, 2`() {
    """
      ${imports()}
      open class A() {
        open fun f(x: Int): Int {
          pre(x > 0) { "greater than 0" }
          val y = x + 1
          return y.post({ it > 0 }) { "greater than 0" }
        }
      }
      
      class B(): A() {
        override fun f(x: Int): Int {
          pre(x >= 0) { "greater or equal to 0" }
          val y = x + 1000
          return y.post({ it > 1 }) { "greater than 1" }
        }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `subtyping, Liskov Substitution Principle, interface`() {
    """
      ${imports()}
      interface A {
        fun f(): Int 
        
        @Law
        fun f_Law(): Int =
          f().post({ it > 0 }) { "greater than 0" }
      }
      
      class B(): A {
        override fun f(): Int = 0
      }
      """(
      withPlugin = {
        failsWith { it.contains("declaration `f` fails to satisfy the post-condition") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with two init blocks`() {
    """
      ${imports()}
      data class A(val n: Int) {
        init {
          pre(n > 0) { "n must be positive" }
        }
        init {
          post({ n > 0 }) { "n must be positive" }
        }
        fun f(x: Int) = x
      }
      
      val wrong = A(0)
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `n must be positive` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with two (wrong) init blocks`() {
    """
      ${imports()}
      data class A(val n: Int) {
        init {
          pre(n > 0) { "n must be positive" }
        }
        init {
          post({ n < 0 }) { "n must be negative" }
        }
        fun f(x: Int) = x
      }
      """(
      withPlugin = {
        failsWith { it.contains("declaration `A` fails to satisfy the post-condition") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with require`() {
    """
      ${imports()}
      data class B(val n: Int) {
        init {
          require(n > 0) { "n must be positive" }
        }
        fun f(x: Int) = x
      }
      
      val wrong = B(0)
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `n must be positive` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with require, field knows about invariant, 1`() {
    """
      ${imports()}
      data class B(val n: Int) {
        init {
          require(n > 0) { "n must be positive" }
        }
        fun f(x: Int): Int {
          pre(x > 0) { "x is positive" }
          val y = x
          return (y + n).post({it > 0}) { "result is positive" }
        }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with require, field knows about invariant, 2`() {
    """
      ${imports()}
      data class B(val n: Int) {
        init {
          require(n > 0) { "n must be positive" }
        }
      }
      
      fun f(b: B, x: Int): Int {
        pre(x > 0) { "x is positive" }
        return (x + b.n).post({it > 0}) { "result is positive" }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with require, secondary constructor`() {
    """
      ${imports()}
      data class B(val n: Int) {
        init {
          require(n > 0) { "n must be positive" }
        }
        constructor() : this(0) { }
        fun f(x: Int) = x
      }
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `n must be positive` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with require, two classes`() {
    """
      ${imports()}
      open class A(val n: Int) {
        init {
          require(n > 0) { "n must be positive" }
        }
        fun f(x: Int) = x
      }
      
      class B(): A(0) { }
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `n must be positive` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `class with require on field, implicit constructor`() {
    """
      ${imports()}
      class A {
        val n = 0
        init {
          require(n > 0) { "n must be positive" }
        }
      }
      
      val x = A()
      """(
      withPlugin = {
        failsWith { it.contains("declaration `A` fails to satisfy the post-condition") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `larger example of invariant`() {
    """
      ${imports()}
      class Positive(val n: Int) {
        init { require(n >= 0) }
      
        operator fun minus(y: Positive): Positive {
          pre(this.n >= y.n) { "ensure positive answer" }
          return Positive(this.n - y.n)
        }
      }
      
      val p = Positive(1) - Positive(2)
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `ensure positive answer` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `enumerations, init blocks ok`() {
    """
      ${imports()}
      enum class Color(val rgb: Int) {
        RED(0xFF0000),
        GREEN(0x00FF00),
        BLUE(0x0000FF);
    
        init {
          require(rgb != 0) { "no zero color" }
        }
      }
      
      val result: Int = Color.RED.rgb.post({ it != 0 }) { "check this" }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `enumerations, init blocks wrong`() {
    """
      ${imports()}
      enum class Cosa(val x: Int) {
        Esto(0),
        Eso(1),
        Aquello(2);
    
        init {
          require(x > 0) { "positive" }
        }
      }
      """(
      withPlugin = { failsWith { it.contains("pre-condition `positive` is not satisfied") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `overloads work right, 1`() {
    """
      ${imports()}
      fun f(x: Int): Int {
        pre(x > 10) { "greater than ten" }
        return 1
      }
      
      fun f(x: List<Int>): Int = 1
      
      val result = f(1)
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `greater than ten` is not satisfied") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `overloads work right, 2`() {
    """
      ${imports()}
      fun f(x: Int): Int {
        pre(x > 10) { "greater than ten" }
        return 1
      }
      
      fun f(x: List<Int>): Int = 1
      
      val result = f(emptyList<Int>())
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `optional arguments, 1`() {
    """
      ${imports()}
      fun f(x: Int = 0): Int {
        pre(x > 10) { "greater than ten" }
        return 1
      }
      """(
      withPlugin = { failsWith { it.contains("has inconsistent default values") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `optional arguments, 2`() {
    """
      ${imports()}
      fun f(x: Int = 1): Int {
        pre(x > 0) { "greater than ten" }
        return 1
      }
      
      val result1 = f()
      val result2 = f(3)
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `optional arguments, only knows pre`() {
    """
      ${imports()}
      fun f(x: Int = 1): Int {
        pre(x > 0) { "greater than ten" }
        return x.post({ it == 1 }) { "is one" }
      }
      """(
      withPlugin = { failsWith { it.contains("fails to satisfy the post-condition") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `parses predicates, Result`() {
    """
      ${imports()}
      
      import kotlin.Result.Companion.success
      import kotlin.Result.Companion.failure
      
      @Law
      inline fun <T> successLaw(x: T): Result<T> =
        success(x).post({ it.isSuccess == true }) { "create a success" }
      
      @Law
      inline fun <T> failureLaw(e: Throwable): Result<T> =
        failure<T>(e).post({ it.isFailure == true }) { "create a failure" }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `parses predicates, Lazy`() {
    """
      ${imports()}
      
      @Law
      inline fun <T> lazyOfLaw(value: T): Lazy<T> =
        lazyOf(value).post({ it.value == value }) { "lazy value is argument" }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `parses predicates, Collection, 1`() {
    """
      ${imports()}
      ${collectionListLaws()}
        
      val problem = emptyList<Int>().map { it + 1 }.first()
      """(
      withPlugin = { failsWith { it.contains("pre-condition `not empty` is not satisfied") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `parses predicates, Collection, 2`() {
    """
      ${imports()}
      ${collectionListLaws()}
        
      val oki = emptyList<Int>().map { it + 1 }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `function reference`() {
    """
      ${imports()}
      ${collectionListLaws()}
      
      fun addOne(n: Int): Int = n + 1
      val problem = emptyList<Int>().map(::addOne).first()
      """(
      withPlugin = { failsWith { it.contains("pre-condition `not empty` is not satisfied") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `parses predicates, Collection, using annotations`() {
    """
      ${imports()}
      
      @Laws
      object ListLaws {
        @Law
        @Post(messages = ["empty list is empty"], formulae = ["(= (int (field kotlin.collections.List.size ${'\\'}${'$'}result)) 0)"], dependencies = ["kotlin.collections.List.size"])
        @Subject(fqName = "kotlin.collections.emptyList")
        inline fun <E> emptyListLaw(): List<E> = emptyList<E>()

        @Law
        @Pre(messages = ["not empty"], formulae = ["(>= (int (field kotlin.collections.List.size this)) 1)"], dependencies = ["kotlin.collections.List.size"])
        @Subject(fqName = "kotlin.collections.first")
        inline fun <E> List<E>.firstLaw(): E = first()
      }
        
      val oki = emptyList<Int>().first()
      """(
      withPlugin = { failsWith { it.contains("pre-condition `not empty` is not satisfied") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `string literals, fail`() {
    """
      ${imports()}
      ${stringLaws()}
      fun bar(name: String): Int {
        pre( name.isNotEmpty() ) { "not empty name" }
        return 2
      }
      val result = bar("")
      """(
      withPlugin = {
        failsWith { it.contains("pre-condition `not empty name` is not satisfied in `bar(\"\")`") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `string literals, ok`() {
    """
      ${imports()}
      ${stringLaws()}
      fun bar(name: String): Int {
        pre( name.isNotEmpty() ) { "not empty name" }
        return 2
      }
      val result = bar("alex")
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `string templates, fail`() {
    """
      ${imports()}
      ${stringLaws()}
      fun bar(name: String): Int {
        pre( name.isNotEmpty() ) { "not empty name" }
        return 2
      }
      // we don't know upfront the length of the expression
      val result = bar("${'$'}{1 + 2}")
      """(
      withPlugin = { failsWith { it.contains("pre-condition `not empty name` is not satisfied") } },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `late initialization, without this`() {
    """
      ${imports()}
      
      class A {
        val thing: Int
        
        public constructor() {
          thing = 0
          post({ it.thing == 0 }) { "thing is zero" }
        }
        
        /* cannot be in 'init' because it's "too early"
           and the compiler says 'thing' is not initialized
        init {
          require(thing == 0) { "thing is zero" }
        }
        */
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `late initialization, with this`() {
    """
      ${imports()}
      
      class A {
        val thing: Int
        
        public constructor() {
          this.thing = 0
          post({ it.thing == 0 }) { "thing is zero" }
        }
      }
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `implicit primary constructor, ok`() {
    """
      ${imports()}
      
      class A {
        val thing: Int = 0
        
        init {
          require(thing == 0) { "thing is zero" }
        }
      }
      
      val example = A()
      """(
      withPlugin = { compilesNoUnreachable },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `implicit primary constructor, fail`() {
    """
      ${imports()}
      
      class A {
        val thing: Int = 1
        
        init {
          require(thing == 0) { "thing is zero" }
        }
      }
      
      val example = A()
      """(
      withPlugin = {
        failsWith { it.contains("declaration `A` fails to satisfy the post-condition") }
      },
      withoutPlugin = { compiles }
    )
  }

  @Test
  fun `implicit primary constructor on object, fail`() {
    """
      ${imports()}
      
      object A {
        val thing: Int = 1
        
        init {
          require(thing == 0) { "thing is zero" }
        }
      }
      
      val example = A.thing
      """(
      withPlugin = {
        failsWith { it.contains("declaration `A` fails to satisfy the post-condition") }
      },
      withoutPlugin = { compiles }
    )
  }
}

private val AssertSyntax.compilesNoUnreachable: Assert.SingleAssert
  get() = compilesWith { !it.contains("unreachable code") }

private fun imports() =
  """
package test

import arrow.analysis.invariant
import arrow.analysis.pre
import arrow.analysis.post
import arrow.analysis.Pre
import arrow.analysis.Post
import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.Subject
import arrow.analysis.unsafeBlock
import arrow.analysis.unsafeCall

 """

private fun collectionListLaws(): String =
  """
@Laws
object CollectionLaws {
  @Law
  inline fun <E> Collection<E>.firstLaw(): E {
    pre(size >= 1) { "not empty" }
    return first()
  }
  @Law
  inline fun <A, B> Collection<A>.mapLaw(transform: (A) -> B): List<B> =
    map(transform).post({ it.size == this.size }) { "size remains after map" }
}

@Laws
object ListLaws {
  @Law
  inline fun <E> emptyListLaw(): List<E> =
    emptyList<E>().post({ it.size == 0 }) { "empty list is empty" }
  @Law
  inline fun <E> List<E>.firstLaw(): E {
    pre(size >= 1) { "not empty" }
    return first()
  }
}
"""

private fun arrayListLaws(): String =
  """
import kotlin.collections.ArrayList
      
@Laws
object ArrayListLaws {
  @Law
  fun <A> ArrayListConstruction(initialCapacity: Int): ArrayList<A> {
    pre( initialCapacity >= 0 ) { "initial capacity should be non-negative" }
    return ArrayList(initialCapacity)
  }
}
"""

private fun stringLaws(): String =
  """
@Law
fun CharSequence.noneLaw(): Boolean =
  none().post({ it == (length <= 0) }) { "none when length is 0" }
@Law
fun CharSequence.isNotEmptyLaw(): Boolean =
  isNotEmpty().post({ it == (length > 0) }) { "not empty when length is > 0" }
"""

// TODO update arrow dependencies to latest to test validated support
private operator fun String.invoke(
  withPlugin: AssertSyntax.() -> Assert,
  withoutPlugin: AssertSyntax.() -> Assert
) {
  assertThis(
    CompilerTest(
      config = {
        newMetaDependencies() // +
        // addPluginOptions(PluginOption("arrow.meta.plugin.compiler", "generatedSrcOutputDir",
        // "value"))
      },
      code = { this@invoke.source },
      assert = { withPlugin() }
    )
  )
  assertThis(
    CompilerTest(
      config = { listOf(addDependencies(analysisLib(System.getProperty("CURRENT_VERSION")))) },
      code = { this@invoke.source },
      assert = { withoutPlugin() }
    )
  )
}
