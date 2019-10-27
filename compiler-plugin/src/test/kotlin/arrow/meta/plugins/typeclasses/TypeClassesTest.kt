package arrow.meta.plugins.typeclasses

import arrow.core.Some
import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class TypeClassesTest {

  @Test
  fun `simple case`() {
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT")
    val arrowCoreData = Dependency("arrow-core-data:0.10.2")
    val codeSnippet = """
      | import arrow.Kind
      | import arrow.given
      | import arrow.core.Some
      | import arrow.core.Option
      | import arrow.extension
      | import arrow.core.ForOption
      | import arrow.core.fix
      | import arrow.core.None
      |
      | //metadebug
      |
      | @extension
      | object OptionMappable : Mappable<ForOption> {
      |   override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> =
      |     when (val o: Option<A> = this.fix()) {
      |       is Some -> Some(f(o.t))
      |       None -> None
      |     }
      | } 
      | 
      | interface Mappable<F> {
      |   fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
      | }
      |
      | object Test {
      |   fun <F> Kind<F, Int>.addOne(M: Mappable<F> = given): Kind<F, Int> =
      |     map { it + 1 }
      | }
      |
      | fun foo(): Option<Int> {
      |   Test.run {
      |     return Some(1).addOne()
      |   }
      | }
      |"""

    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations, arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        quoteOutputMatches(
          """
          | import arrow.Kind
          | import arrow.given
          | import arrow.core.Some
          | import arrow.core.Option
          | import arrow.extension
          | import arrow.core.ForOption
          | import arrow.core.fix
          | import arrow.core.None
          | 
          | //meta: <date>
          | 
          | @extension
          | object OptionMappable : Mappable<ForOption> {
          |   override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> =
          |     when(val o: Option<A> = this.fix()) {
          |       is Some -> Some(f(o.t))
          |       None -> None
          |     }
          | }
          | 
          | interface Mappable<F> {
          |   fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
          | }
          | 
          | object Test {
          |   fun <F> Kind<F, Int>.addOne(M: Mappable<F> = given): Kind<F, Int> =
          |     M.run { map { it + 1 } }
          | }
          | 
          | fun foo(): Option<Int> {
          |   Test.run {
          |     return Some(1).addOne()
          |   }
          | }
          |""".source)
      }
    ))

    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations, arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        "foo()".source.evalsTo(Some(2))
      }
    ))
  }
}