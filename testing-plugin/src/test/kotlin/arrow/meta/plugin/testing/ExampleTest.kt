package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.Code.Source
import org.junit.Test

class ExampleTest {

  @Test
  fun `checks that compiles`() {
    assertThis(CompilerTest(
      code = {
        """
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
      },
      assert = {
        allOf(compiles)
      }
    ))
  }

  @Test
  fun `check an expression evaluation`() {
    assertThis(CompilerTest(
      code = {
        """
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
      },
      assert = {
        allOf("hello()".source.evalsTo("Hello world!"))
      }
    ))
  }

  @Test
  fun `check that fails`() {
    assertThis(CompilerTest(
      code = {
        """
        | classsss Error
        | 
        """.source
      },
      assert = {
        allOf(fails)
      }
    ))
  }

  @Test
  fun `check that emits an error diagnostic when compilation fails`() {
    assertThis(CompilerTest(
      code = {
        """
        | classsss Error
        | 
        """.source
      },
      assert = {
        allOf(failsWith { it.contains("Expecting a top level declaration") })
      }
    ))
  }

  @Test
  fun `eval a variable`() {
    assertThis(CompilerTest(
      code = {
        """
        |
        | val x: String = "Hello world!"
        | 
        """.source
      },
      assert = {
        allOf("x".source.evalsTo("Hello world!"))
      }
    ))
  }

  @Test
  fun `allows several sources with names to add associated asserts`() {
    assertThis(CompilerTest(
      code = {
        sources(
          Source(
            filename = "LowerCase.kt",
            text = """
              |
              | val x: String = "hello world!"
              | 
              """
          ),
          Source(
            filename = "UpperCase.kt",
            text = """
              |
              | val y: String = "HELLO WORLD!"
              | 
              """
          ))
      },
      assert = {
        allOf(
          Source(filename = "LowerCaseKt", text = "x").evalsTo("hello world!"),
          Source(filename = "UpperCaseKt", text = "y").evalsTo("HELLO WORLD!")
        )
      }
    ))
  }

  @Test
  fun `allows several sources without names because they don't matter`() {
    assertThis(CompilerTest(
      code = {
        sources(
          """
          |
          | val x: String = "hello world!"
          | 
          """.source,
          """
          |
          | val y: String = "HELLO WORLD!"
          | 
          """.source)
      },
      assert = {
        allOf(compiles)
      }
    ))
  }
}
