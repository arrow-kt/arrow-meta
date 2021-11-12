package arrow.meta.plugins.analysis.java

import org.junit.jupiter.api.Test

class AnalysisTests {

  @Test
  fun `first test, ok`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        pre(x > 0, () -> "x must be positive");
        return post(x + 1, (r) -> r > 0, () -> "result is positive");
      } 
    }
    """(
      withPlugin = { succeeded() },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `first test, ok, using assert`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        assert x > 0 : "x must be positive";
        return post(x + 1, (r) -> r > 0, () -> "result is positive");
      } 
    }
    """(
      withPlugin = { succeeded() },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `first test, fails`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        pre(x > 0, () -> "x must be positive");
        return post(x - 1, (r) -> r > 0, () -> "result is positive");
      } 
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("fails to satisfy the post-condition")
      },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `first test, post at beginning, fails`() {
    """
    import arrow.analysis.RefinementDSLKt;
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        pre(x > 0, () -> "x must be positive");
        post((Integer r) -> r > 0, () -> "result is positive");
        return x - 1;
      } 
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("fails to satisfy the post-condition")
      },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `ternary conditional`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        pre(x > 0, () -> "x must be positive");
        final int y = (x > 2) ? x + 1 : x - 1;
        return post(y, (r) -> r > 0, () -> "result is positive");
      } 
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("fails to satisfy the post-condition")
      },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `conditional, post at beginning, ok`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        pre(x > 0, () -> "x must be positive");
        post((Integer r) -> r > 0, () -> "result is positive");
        if (x > 2) {
          return x + 1;
        } else {
          return x + 2;
        }
      } 
    }
    """(
      withPlugin = { succeeded() },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `conditional, post at beginning, fail`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Example { 
      public int f(int x) { 
        pre(x > 0, () -> "x must be positive");
        post((Integer r) -> r > 0, () -> "result is positive");
        if (x > 2) {
          return x + 1;
        } else {
          return x - 1;
        }
      } 
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("fails to satisfy the post-condition")
      },
      withoutPlugin = { succeeded() }
    )
  }
}
