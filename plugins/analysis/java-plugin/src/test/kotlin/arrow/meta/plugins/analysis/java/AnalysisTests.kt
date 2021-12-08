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

  @Test
  fun `class invariant`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Positive {
      
      private final int n;
      public int getValue() {
        return n;
      }
      
      public Positive(int value) {
        pre(value >= 0, () -> "value is positive");
        this.n = value;
      }
      
      {
        post((Positive r) -> r.getValue() >= 0, () -> "result is positive");
      }
    }
    """(
      withPlugin = { succeeded() },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `class invariant, wrong`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Positive {
      
      private int n;
      public int getValue() {
        return n;
      }
      
      public Positive(int value) {
        pre(value >= 0, () -> "value is positive");
        this.n = value;
      }
      
      static void main() {
        Positive x = new Positive(-1);
      }
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("pre-condition `value is positive` is not satisfied")
      },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `class invariant, on methods, ok`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Positive {
      
      private final int n;
      public int getValue() {
        return n;
      }
      
      public Positive(int value) {
        pre(value >= 0, () -> "value is positive");
        this.n = value;
      }
      
      {
        post((Positive r) -> r.getValue() >= 0, () -> "result is positive");
      }
      
      public Positive add(Positive other) {
        return new Positive(this.getValue() + other.getValue());
      }
    }
    """(
      withPlugin = { succeeded() },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `class invariant, on methods, wrong`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Positive {
      
      private final int n;
      public int getValue() {
        return n;
      }
      
      public Positive(int value) {
        pre(value >= 0, () -> "value is positive");
        this.n = value;
      }
      
      {
        post((Positive r) -> r.getValue() >= 0, () -> "result is positive");
      }
      
      public Positive subtract(Positive other) {
        return new Positive(this.getValue() - other.getValue());
      }
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("pre-condition `value is positive` is not satisfied")
      },
      withoutPlugin = { succeeded() }
    )
  }

  @Test
  fun `class invariant with assert, on methods, wrong`() {
    """
    import static arrow.analysis.RefinementDSLKt.*;
    
    final class Positive {
      
      private final int n;
      
      public Positive(int value) {
        pre(value >= 0, () -> "value is positive");
        this.n = value;
      }
      
      public int getValue() {
        return n;
      }
      
      {
        assert this.getValue() >= 0 : "result is positive";
      }
      
      public Positive subtract(Positive other) {
        return new Positive(this.getValue() - other.getValue());
      }
    }
    """(
      withPlugin = {
        failed()
        hadErrorContaining("pre-condition `result is positive` is not satisfied")
      },
      withoutPlugin = { succeeded() }
    )
  }
}
