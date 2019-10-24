package arrow.meta.plugins.comprehensions

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.interpreter
import org.junit.Test

class ComprehensionsTest {

  companion object {
    const val IO_CLASS_4_TESTS = """
      | import kotlin.reflect.KProperty
      |
      | //metadebug
      |
      | class IO<A>(val value: A) {
      |
      |   operator fun getValue(value: Any?, property: KProperty<*>): A = TODO()
      |
      |   fun <B> flatMap(f: (A) -> IO<B>): IO<B> =
      |     f(value)
      |
      |   companion object {
      |     fun <A> fx(f: IO.Companion.() -> A): IO<A> = TODO()
      |     fun <A> just(a: A): IO<A> = IO(a)
      |   }
      | }
      """
    val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin")))
  }

  @Test
  fun `simple case`() {
    val compilerTest = CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin))
      },
      code = {
        """
        $IO_CLASS_4_TESTS
        |
        | fun test(): IO<Int> =
        |   IO.fx {
        |     val a: Int by IO(1)
        |     val b: Int by IO(2)
        |     a + b
        |   }
        |   
        |""".source
      },
      assert = {
        listOf(
          quoteOutputMatches(
            """
            $IO_CLASS_4_TESTS
            |
            | fun test(): IO<Int> =
            |   IO(1).flatMap { a : Int ->
            |     IO(2).flatMap { b : Int ->
            |       IO.just(a + b)
            |     }
            |   }
            |   
            |""".source),
          "test().value".source.evalsTo(3)
        )
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `simple case with type inference`() {
    val compilerTest = CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin))
      },
      code = {
        """
        $IO_CLASS_4_TESTS
        |
        | fun test(): IO<Int> =
        |   IO.fx {
        |     val a by IO(1)
        |     val b by IO(2)
        |     a + b
        |   }
        |   
        |""".source
      },
      assert = {
        listOf(
          quoteOutputMatches(
            """
            $IO_CLASS_4_TESTS
            |
            | fun test(): IO<Int> =
            |   IO(1).flatMap { a  ->
            |     IO(2).flatMap { b  ->
            |       IO.just(a + b)
            |     }
            |   }
            |   
            |""".source),
          "test().value".source.evalsTo(3)
        )
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `nested case with type inference`() {
    val compilerTest = CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin))
      },
      code = {
        """
        $IO_CLASS_4_TESTS
        |
        | fun test(): IO<Int> =
        |   IO.fx {
        |     val a by IO.fx {
        |       val a by IO(1)
        |       val b by IO(2)
        |       a + b
        |     }
        |     val b by IO.fx {
        |       val a by IO(3)
        |       val b by IO(4)
        |       a + b
        |     }
        |     a + b
        |   }
        |   
        |""".source
      },
      assert = {
        listOf(
          quoteOutputMatches(
            """
            $IO_CLASS_4_TESTS
            |
            | fun test(): IO<Int> = 
            |   IO(1).flatMap { a ->
            |     IO(2).flatMap { b ->
            |       IO.just(a + b)
            |     }
            |   }.flatMap { a -> 
            |     IO(3).flatMap { a -> 
            |       IO(4).flatMap { b -> 
            |         IO.just(a + b)  
            |       }
            |     }.flatMap { b ->
            |       IO.just(a + b)
            |     }
            |   }
            |   
            |""".source),
          "test().value".source.evalsTo(10)
        )
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `mixed properties and expressions`() {
    val compilerTest = CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin))
      },
      code = {
        """
        $IO_CLASS_4_TESTS
        |
        | fun test(): IO<Int> =
        |   IO.fx {
        |     val a by IO(1)
        |     val t = a + 1
        |     val b by IO(2)
        |     val y = a + b
        |     val f by IO(3)
        |     val n = a + 1
        |     val g by IO(4)
        |     y + f + g + t + n
        |   }
        |   
        |""".source
      },
      assert = {
        listOf(
          quoteOutputMatches(
            """
            $IO_CLASS_4_TESTS
            |
            | fun test(): IO<Int> =
            |   IO(1).flatMap { a -> 
            |     val t = a + 1
            |     IO(2).flatMap { b -> 
            |       val y = a + b
            |       IO(3).flatMap { f -> 
            |         val n = a + 1
            |         IO(4).flatMap { g -> 
            |           IO.just(y + f + g + t + n)  
            |         }
            |       }
            |     } 
            |   }
            |   
            |""".source),
          "test().value".source.evalsTo(14)
        )
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `just`() {
    val compilerTest = CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin))
      },
      code = {
        """
        $IO_CLASS_4_TESTS
        |
        | fun test(): IO<Int> =
        |   IO.fx { 1 + 1 }
        |
        |""".source
      },
      assert = {
        listOf(
          quoteOutputMatches(
            """
            $IO_CLASS_4_TESTS
            |
            | fun test(): IO<Int> =
            |   IO.just(1 + 1)
            |   
            |""".source),
          "test().value".source.evalsTo(2)
        )
      }
    )
    compilerTest.run(interpreter)
  }

  @Test
  fun `unresolved reference error`() {
    val compilerTest = CompilerTest(
      config = {
        listOf(addCompilerPlugins(compilerPlugin))
      },
      code = {
        """
        $IO_CLASS_4_TESTS
        |
        | fun test(): IO<Int> =
        |   IO.fx { a + 1 }
        |
        |""".source
      },
      assert = {
        listOf(failsWith { it.contains("Unresolved reference: a") })
      }
    )
    compilerTest.run(interpreter)
  }

//  @Test
//  fun `Does not break other delegations`() {
//    val compilerTest = CompilerTest(
//      config = {
//        listOf(addCompilerPlugins(compilerPlugin))
//      },
//      code = {
//        """
//        $IO_CLASS_4_TESTS
//        |
//        | fun test(): IO<Int> =
//        |   IO.fx {
//        |     val a by IO(1)
//        |     val b: Int by lazy { 2 }
//        |     a + b
//        |   }
//        |
//        |""".source
//      },
//      assert = {
//        listOf(
//          quoteOutputMatches(
//            """
//            $IO_CLASS_4_TESTS
//            |
//            | fun test(): IO<Int> =
//            |   IO(1).flatMap { a ->
//            |     lazy { 2 }.flatMap { b : Int ->
//            |       IO.just(a + b)
//            |      }
//            |   }
//            |
//            |""".source)
//        )
//      }
//    )
//
//    compilerTest.run(interpreter)
//  }
}