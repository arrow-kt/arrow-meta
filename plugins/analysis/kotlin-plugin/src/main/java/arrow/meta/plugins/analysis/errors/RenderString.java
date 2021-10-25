package arrow.meta.plugins.analysis.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer;
import org.jetbrains.kotlin.diagnostics.rendering.RenderingContext;

public class RenderString implements DiagnosticParameterRenderer<String> {

    static final RenderString instance = new RenderString();

    @NotNull
    @Override
    public String render(String o, @NotNull RenderingContext renderingContext) {
        return o;
    }
}
