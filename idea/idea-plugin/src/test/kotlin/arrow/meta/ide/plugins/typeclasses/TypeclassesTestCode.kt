package arrow.meta.ide.plugins.typeclasses

/**
 * We should be able to get TypeHinting for `foo`s ReturnType
 */
internal object TypeclassesTestCode {
  val c1 = """
      | import arrow.Kind
      | import arrow.given
      | import arrow.core.Some
      | import arrow.core.Option
      | import arrow.extension
      | import arrow.core.ForOption
      | import arrow.core.fix
      | import arrow.core.None
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

  val compilerResult = """
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
          |"""
}