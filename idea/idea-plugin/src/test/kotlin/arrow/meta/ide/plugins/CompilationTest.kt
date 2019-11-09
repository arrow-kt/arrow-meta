package arrow.meta.ide.plugins

import arrow.meta.ide.compile.CompilationStatus
import arrow.meta.ide.compile.compile
import arrow.meta.ide.testing.env.IdeHeavyTestSetUp
import org.junit.Test

class CompilationTest : IdeHeavyTestSetUp() {
  @Test
  fun testCompilation() {
    val code = """
      fun main() {
        println("hello world")
      }
    """

    // this prints an exception to stdout
    val result = compile(code)
    assertTrue("compilation of a snippet must succeed", result.actualStatus == CompilationStatus.OK)
  }
}
