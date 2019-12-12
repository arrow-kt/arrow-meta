package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import io.kotlintest.specs.AnnotationSpec

class HigherkindTest  : AnnotationSpec() {

  @Test
  fun `initial test`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
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
        """.source
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
          """.source)
      }
    ))
  }
}
