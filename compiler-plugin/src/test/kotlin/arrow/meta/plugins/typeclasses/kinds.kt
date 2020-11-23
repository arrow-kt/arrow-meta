package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

const val prelude = """
package test
import arrow.*
import arrowx.Kind
import arrowx.Kinded

interface Functor<F> {
    interface Ops<F, A> {
        val value: Kind<F, A>
        fun <B> fmap(f: (A) -> B): Kind<F, B>
    }
}

"""

class KindsTest {

  @Test
  @Disabled
  fun `Platform types can be kinded ad-hoc by proof`() {
    val codeSnippet = """
      $prelude
        object `List(_)`
        typealias ListOf<A> = Kind<`List(_)`, A>

        @Coercion
        fun <A> List<A>.unfix(): ListOf<A> =
            Kinded(this)
        
        @Coercion
        fun <A> ListOf<A>.fix(): List<A> =
            (this as Kinded).value as List<A>
        
        class ListFunctor<A>(override val value: ListOf<A>) : Functor.Ops<`List(_)`, A> {
            override fun <B> fmap(f: (A) -> B): ListOf<B> =
                value.fix().map(f)
        }
        
        @Extension
        fun <A> List<A>.functor(): Functor.Ops<`List(_)`, A> =
            ListFunctor(unfix())

        val result: List<Int> = listOf(1, 2, 3).fmap { it }
        //val result2: List<Int> = listOf(1, 2, 3).functor().fmap { it }
      """
    assertThis(CompilerTest(
      config = {
        metaDependencies
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf("result".source.evalsTo(listOf(1, 2, 3)))
      }
    ))
  }

}
