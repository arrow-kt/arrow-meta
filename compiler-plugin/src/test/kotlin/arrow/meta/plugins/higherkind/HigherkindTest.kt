package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class HigherkindTest {

  @Test
  fun `initial test`() {
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:${System.getProperty("CURRENT_VERSION")}")

    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(compilerPlugin) + addDependencies(arrowAnnotations)
      },
      code = {
        """
        | import arrow.higherkind
        | 
        | //metadebug
        | 
        | @higherkind
        | class Id2<out A>(val value: A)
        | 
        | val x: Id2Of<Int> = Id2(1)
        | 
        """
      },
      assert = {
        quoteOutputMatches(
          """
          | import arrow.higherkind
          | 
          | //meta: <date>
          | 
          | @arrow.synthetic class ForId2 private constructor() { companion object }
          | @arrow.synthetic typealias Id2Of<A> = arrow.Kind<ForId2, A>
          | @arrow.synthetic typealias Id2KindedJ<A> = arrow.HkJ<ForId2, A>
          | @arrow.synthetic fun <A> Id2Of<A>.fix(): Id2<A> =
          |   this as Id2<A>
          | @arrow.synthetic @higherkind /* empty? */class Id2 <out A> public constructor (val value: A) : Id2Of<A> {}
          | 
          | val x: Id2Of<Int> = Id2(1)
          | 
          """)
      }
    ))
  }
}
