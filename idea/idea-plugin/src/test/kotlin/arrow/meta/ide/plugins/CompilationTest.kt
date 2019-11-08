package arrow.meta.ide.plugins

import arrow.meta.ide.testing.env.IdeHeavyTestSetUp
import arrow.meta.ide.testing.env.types.HeavyTestSyntax.compile
import com.tschuchort.compiletesting.KotlinCompilation

class CompilationTest : IdeHeavyTestSetUp() {
  fun testCompilation() {
    val code = """
      fun main() {
        println("hello world")
      }
    """

    // this prints an exception to stdout
    val result = compile(code)
    assertTrue("compilation of a snippet must succeed", result.exitCode == KotlinCompilation.ExitCode.OK)
  }
} 
