package arrow.meta.plugin.testing

import org.junit.Test

class ExampleTest {

  @Test
  fun `checks that compiles`() {
    val compilerTest = CompilerTest(
      code = {
        """
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
      },
      assert = {
        listOf(compiles)
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `check an expression evaluation`() {
    val compilerTest = CompilerTest(
      code = {
        """
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
      },
      assert = {
        listOf(
          "hello()".source.evalsTo("Hello world!"))
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `check that fails`() {
    val compilerTest = CompilerTest(
      code = {
        """
        | classsss Error
        | 
        """.source
      },
      assert = {
        listOf(fails)
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `check that emits an error diagnostic when compilation fails`() {
    val compilerTest = CompilerTest(
      code = {
        """
        | classsss Error
        | 
        """.source
      },
      assert = {
        listOf(
          failsWith { it.contains("Expecting a top level declaration") })
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `checks the meta debug output given a configuration`() {
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
    val arrowAnnotations = Dependency("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT")

    val compilerTest = CompilerTest(
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
        """.source
      },
      assert = {
        listOf(
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
          """.source))
      }
    )
    compilerTest.run(interpreter)
  }
}