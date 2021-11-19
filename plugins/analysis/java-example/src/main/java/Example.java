import static arrow.analysis.RefinementDSLKt.post;
import static arrow.analysis.RefinementDSLKt.pre;

public class Example {
    public int f(int x) {
        pre(x > 0, () -> "x mut be positive");
        return post(x + 1, r -> r > 100, () -> "result is positive");
    }
}
