package arrow.meta.plugins.typeclasses

import arrow.meta.internal.Noop
import arrow.meta.plugin.testing.Assert
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class WithTests {

  // TODO: Port the remaining Given Tests
  @Test
  fun `coherent polymorphic identity`() {
    withTest(
      source = """
        @Given internal val x = "yes!"
        val result = with<String>()
      """,
      expected = "result" to "yes!"
    )
  }

  @Test
  fun `synthetic member access`() {
    withTest(
      source = """
    interface Container<F, A> {
      fun first(): A?
    }
    
    interface Ability<F> {
      fun <A> Container<F, A>.add(a: A): Container<F, A>
    }
    
    fun <F, A> @with<Ability<F>>
    Container<F, A>.addFirstOrElse(
      alternative: () -> Container<F, A> = { this }
    ): Container<F, A> =
      first()?.let { add(it) } ?: or()
    """
    ) {
      listOf(compiles)
    }
  }


  @Test
  fun `single receiver`() {
    withTest(
      source = """
        interface Plus<A> {
          operator fun A.plus(other: A): A
        }
        
        @Given
        object PlusInt : Plus<Int> {
          override fun Int.plus(other: Int): Int = 
            this + other
        }
        
        infix fun <A> @with<Plus<A>> A.combine(other: A) =
            this + other
        
        val result = 3 combine 4
      """,
      expected = "result" to 7
    )
  }

  @Test
  fun `single receiver with polymorphic type parameter`() {
    withTest(
      """
        
        fun List<String>.hello() = 
          map { it.run { combine(empty()) } }
        
        //fun <A> @with<Monoid<A>> List<A>.addEmpty(): List<A> =
        //    map { it.combine(empty()) }
            
        // val result = listOf("3").combineEmpty() 
      """
      //, "result" to listOf("3")
    ) {
      listOf(
        compiles
      )
    }
  }

  @Test
  fun `multiple receiver`() {
    withTest(
      source = """
        fun <A, B>
          @with<Monoid<A>, Monoid<B>>
          Pair<A, B>.combineEmpty(): Pair<A, B> =
            first.combine(empty()) to second.combine(empty())
            
        val result = ("Hello" to 0).combineEmpty() 
      """,
      expected = "result" to ("Hello" to 0)
    )
  }

  @Test
  fun `multiple receivers with collections`() {
    withTest("""
      fun <A, B> @with<Monoid<A>, Monoid<B>> List<Pair<A, B>>.addEmpty() = 
        this.map { (a, b) -> a.combine(this.empty()) to b.combine(this.empty()) }
        
      val result = listOf("Hello" to 0).addEmpty()
    """.trimIndent(),
      expected = "result" to listOf("Hello" to 0)
    )
  }

  @Test
  fun `multiple receivers with collections without this`() {
    withTest("""
      fun <A, B> @with<Monoid<A>, Monoid<B>> List<Pair<A, B>>.addEmpty() = 
        map { (a, b) -> a.combine(empty()) to b.combine(empty()) }
        
      val result = listOf("Hello" to 0).addEmpty()
    """.trimIndent(),
      expected = "result" to listOf("Hello" to 0)
    )
  }

  private fun withTest(source: String, expected: Pair<String, Any?>? = null, asserts: CompilerTest.Companion.() -> List<Assert.SingleAssert> = Noop.emptyList1()) {
    val codeSnippet = """
       package test
       import arrowx.*
          
       @Given
       internal val IntMonoid: Monoid<Int>
         get() = object : Monoid<Int> {
           override fun Int.combine(other: Int): Int =
             this + other
      
           override fun empty(): Int = 0
         }
          
       $source
      """
    assertThis(CompilerTest(
      config = {
        metaDependencies
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf(
          asserts(this) +
            listOfNotNull(expected?.run { first.source.evalsTo(second) })
        )
      }
    ))
  }

}