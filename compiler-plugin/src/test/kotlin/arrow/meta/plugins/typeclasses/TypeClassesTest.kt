package arrow.meta.plugins.typeclasses

import arrow.core.Some
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class TypeClassesTest {

  @Test
  fun `simple case`() {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    val codeSnippet = """
       import arrowx.*
       import arrow.higherkind
       import arrow.Proof
       import arrow.TypeProof.*
      
       //metadebug
       
        interface Applicative<F> {
          fun <A> just(a: A): Kind<F, A>
          fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
        }

        class ForId
        
        @Proof(Subtyping)
        fun <A> Kind<ForId, A>.fix(): Id<A> =
          (this as Kinded).value as Id<A>
          
        @Proof(Subtyping)
        fun <A> Id<A>.unfix(): Kind<ForId, A> =
          Kinded(this)
        
        class Id<out A>(val value: A) {
          companion object
        }
        
        @Proof(Extension)
        fun Id<*>.applicative(): Applicative<ForId> =
          object : Applicative<ForId> {
            override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Kind<ForId, B> =
              TODO()
            override fun <A> just(a: A): Kind<ForId, A> =
              TODO()
          }
        
        fun foo(): Id<Int> = Id(1).map { it + 1 }
        
        fun f(): Int {
          val x : Id<Int> = foo()
          return x.value
        }
      """

    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf("f()".source.evalsTo(2))
      }
    ))
  }
}

