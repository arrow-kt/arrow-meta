package arrow.meta.plugins.analysis.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap;

import static arrow.meta.plugins.analysis.errors.MetaErrors.*;

public class MetaDefaultErrorMessagesJvm implements DefaultErrorMessages.Extension {

    public static final MetaDefaultErrorMessagesJvm INSTANCE = new MetaDefaultErrorMessagesJvm();

    private static final DiagnosticFactoryToRendererMap MAP = new DiagnosticFactoryToRendererMap("JVM");

    @NotNull
    @Override
    public DiagnosticFactoryToRendererMap getMap() {
        return MAP;
    }

    static {
        MAP.put(InconsistentBodyPre, "{0}", RenderString.instance);
        MAP.put(UnsatBodyPost, "{0}", RenderString.instance);
        MAP.put(UnsatCallPre, "{0}", RenderString.instance);
        MAP.put(InconsistentCallPost, "{0}", RenderString.instance);
        MAP.put(InconsistentConditions, "{0}", RenderString.instance);
        MAP.put(InconsistentInvariants, "{0}", RenderString.instance);
        MAP.put(UnsatInvariants, "{0}", RenderString.instance);
        MAP.put(LiskovProblem, "{0}", RenderString.instance);
        MAP.put(ErrorParsingPredicate, "{0}", RenderString.instance);
        MAP.put(UnsupportedElement, "{0}", RenderString.instance);
        MAP.put(AnalysisException, "{0}", RenderString.instance);
    }
}
