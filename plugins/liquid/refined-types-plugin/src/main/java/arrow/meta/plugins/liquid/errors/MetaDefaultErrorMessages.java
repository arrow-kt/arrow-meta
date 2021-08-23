package arrow.meta.plugins.liquid.errors;

import arrow.meta.plugins.liquid.phases.errors.FormulaRendererKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap;

import static arrow.meta.plugins.liquid.errors.MetaErrors.*;

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
        MAP.put(InconsistentBodyPre,
                "{0} has inconsistent pre-conditions: {1}",
                FormulaRendererKt.RenderDeclaration, FormulaRendererKt.RenderFormula);
        MAP.put(UnsatBodyPost,
                "{0} fails to satisfy the post-condition: {1}",
                FormulaRendererKt.RenderDeclaration, FormulaRendererKt.RenderFormula);
        MAP.put(UnsatCallPre,
                "call to {0} fails to satisfy its pre-conditions: {1}",
                FormulaRendererKt.RenderCall, FormulaRendererKt.RenderFormula);
        MAP.put(InconsistentCallPost,
                "unreachable code due to post-conditions: {1}",
                FormulaRendererKt.RenderCall, FormulaRendererKt.RenderFormula);
        MAP.put(InconsistentConditions,
                "unreachable code due to conflicting conditions: {1}",
                FormulaRendererKt.RenderFormula);
    }
}
