package arrow.meta.plugins.liquid.errors;

import arrow.meta.plugins.liquid.phases.errors.FormulaRendererKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap;

import static arrow.meta.plugins.liquid.errors.MetaErrors.*;
import static arrow.meta.plugins.liquid.phases.errors.FormulaRendererKt.RenderString;

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
        MAP.put(InconsistentBodyPre, "{0}", RenderString);
        MAP.put(UnsatBodyPost, "{0}", RenderString);
        MAP.put(UnsatCallPre, "{0}", RenderString);
        MAP.put(InconsistentCallPost, "{0}", RenderString);
        MAP.put(InconsistentConditions, "{0}", RenderString);
        MAP.put(InconsistentInvariants, "{0}", RenderString);
        MAP.put(UnsatInvariants, "{0}", RenderString);
        MAP.put(ErrorParsingPredicate, "{0}", RenderString);
    }
}

