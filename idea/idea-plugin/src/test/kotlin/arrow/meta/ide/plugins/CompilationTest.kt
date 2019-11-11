package arrow.meta.ide.plugins

import arrow.meta.Compilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.junit.Assert.assertEquals
import org.junit.Test

class CompilationTest {
  @Test
  fun `compilation of a snippet must succeed`() {
    val code = """
      fun main() {
        println("hello world")
      }
    """

    val result = Compilation().compile(code)
    assertEquals(ExitCode.OK, result.exitCode)
  }
} 
