package arrow.meta.plugins.analysis.java

import org.junit.jupiter.api.Test

class AnalysisTests {

  @Test
  fun `first test`() {
    ("HelloWorld" to
      """
      import static arrow.analysis.RefinementDSLKt.*;
      
      final class HelloWorld { 
        public int f(int x) { 
          pre(x > 0, () -> "x must be positive");
          return post(x + 1, (r) -> r > 0, () -> "result is positive");
        } 
      }
    """)(
      withPlugin = {
        succeeded()
        hadWarningContaining("Hello")
      },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `two files`() {
    mapOf(
      "HelloWorld" to "final class HelloWorld { public int f(int x) { return x + 1; } }",
      "SecondFile" to "final class SecondFile { public int f(int x) { return x + 1; } }"
    )(
      withPlugin = {
        succeeded()
        hadWarningContaining("Hello")
      },
      withoutPlugin = { succeeded() }
    )
  }
}
