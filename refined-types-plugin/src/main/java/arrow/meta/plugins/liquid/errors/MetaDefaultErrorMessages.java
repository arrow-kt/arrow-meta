package arrow.meta.plugins.liquid.errors;

import arrow.meta.plugins.liquid.phases.errors.FormulaRendererKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap;

import static arrow.meta.plugins.liquid.errors.MetaErrors.UnsatCall;

public class MetaDefaultErrorMessages implements DefaultErrorMessages.Extension {
    @NotNull
    @Override
    public DiagnosticFactoryToRendererMap getMap() {
        return MAP;
    }

    @NotNull
    public static final DiagnosticFactoryToRendererMap MAP =
            new DiagnosticFactoryToRendererMap("Arrow Liquid Expressions");


    static {
        MAP.put(UnsatCall, "call to {0} fails to satisfy constraints: {1}", FormulaRendererKt.RenderCall, FormulaRendererKt.RenderFormula);
    }
}

