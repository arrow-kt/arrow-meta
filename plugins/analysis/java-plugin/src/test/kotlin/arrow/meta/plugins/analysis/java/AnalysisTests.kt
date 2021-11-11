package arrow.meta.plugins.analysis.java

import org.junit.jupiter.api.Test

class AnalysisTests {

  @Test
  fun `first test, ok`() {
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
      withPlugin = { succeeded() },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `first test, fails`() {
    ("HelloWorld" to
      """
      import static arrow.analysis.RefinementDSLKt.*;
      
      final class HelloWorld { 
        public int f(int x) { 
          pre(x > 0, () -> "x must be positive");
          return post(x - 1, (r) -> r > 0, () -> "result is positive");
        } 
      }
    """)(
      withPlugin = {
        failed()
        hadErrorContaining("fails to satisfy the post-condition")
      },
      withoutPlugin = { succeeded() }
    )
  }
}
