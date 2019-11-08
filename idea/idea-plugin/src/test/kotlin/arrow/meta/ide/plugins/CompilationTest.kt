package arrow.meta.ide.plugins

import arrow.CompilationStatus
import arrow.compile
import arrow.meta.ide.testing.env.IdeHeavyTestSetUp

class CompilationTest : IdeHeavyTestSetUp() {
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
