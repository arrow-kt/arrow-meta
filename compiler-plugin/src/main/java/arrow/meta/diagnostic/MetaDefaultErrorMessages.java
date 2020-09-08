package arrow.meta.diagnostic;

import arrow.meta.plugins.proofs.phases.Proof;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticParameterRenderer;

import static arrow.meta.diagnostic.MetaErrors.*;

public class MetaDefaultErrorMessages implements DefaultErrorMessages.Extension {
    @NotNull
    @Override
    public DiagnosticFactoryToRendererMap getMap() {
        return MAP;
    }

    @NotNull
    public static final DiagnosticFactoryToRendererMap MAP =
            new DiagnosticFactoryToRendererMap("Arrow Meta");


    static {
        MAP.put(PublishedInternalOrphan,
                "Internal overrides of proofs are not permitted to be published, as they break coherent proof resolution over the kotlin ecosystem. Please remove the @PublishedApi annotation."
        );
        MAP.put(AmbiguousProof,
                "AmbigousExtensionProof", null, null);
        MAP.put(OwnershipViolatedProof,
                "OwnershipViolatedProof", (DiagnosticParameterRenderer<? super Proof>) null);
        MAP.put(UnresolvedGivenProofs, "UnresolvedGivenProofs", null, null);
    }
}
