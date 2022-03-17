import static arrow.analysis.RefinementDSLKt.post;
import static arrow.analysis.RefinementDSLKt.pre;

public class Example {
    public int f(int x) {
        pre(x > 0, () -> "x must be positive");
        return post(x + 1, r -> r > 0, () -> "result is positive");
    }
}
