package arrow.meta.plugins.typeclasses

import arrow.core.Some
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Ignore
import org.junit.Test

class TypeClassesTest {

  // REASONS OF BEING IGNORED:
  // It raises an error: extension not found for Mappable<F>
  // It will be replaced by Type Proofs
  @Ignore
  @Test
  fun `simple case`() {
    val arrowVersion = System.getProperty("ARROW_VERSION")
    val arrowCoreData = Dependency("arrow-core-data:$arrowVersion")
    val codeSnippet =
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
      """

    assertThis(CompilerTest(
      config = {
        metaDependencies + addDependencies(arrowCoreData)
      },
      code = {
        codeSnippet.source
      },
      assert = {
        allOf(
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
            """.source
          ),
          "foo()".source.evalsTo(Some(2))
        )
      }
    ))
  }
}
