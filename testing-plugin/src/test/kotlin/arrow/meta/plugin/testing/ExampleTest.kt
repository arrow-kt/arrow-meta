package arrow.meta.plugin.testing

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
}
