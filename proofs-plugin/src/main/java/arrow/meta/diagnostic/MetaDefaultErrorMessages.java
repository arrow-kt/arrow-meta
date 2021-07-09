package arrow.meta.diagnostic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap;

import static arrow.meta.diagnostic.MetaErrors.*;
import static arrow.meta.phases.analysis.diagnostic.RenderersKt.*;

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
                "This {0} has following conflicting proof/s: {1}.\nPlease disambiguate resolution, by either declaring only one internal orphan / public proof over the desired type/s or remove conflicting proofs from the project.", RenderProof, RenderProofs);
        MAP.put(OwnershipViolatedProof,
                "This {0} violates ownership rules, because public Proofs over 3rd party Types break coherence over the kotlin ecosystem. One way to solve this is to declare the Proof as an internal orphan.", RenderProof);
        MAP.put(UnresolvedGivenProof, "This GivenProof on the type {0} can't be semi-inductively resolved. Please verify that all parameters have default value or that other injected given values have a corresponding proof.", RenderTypes);
        MAP.put(UnresolvedGivenCallSite, "There is no Proof for this type {1} to resolve this call. Either define a corresponding GivenProof or provide an evidence explicitly at this call-site.", null, RenderTypes);
    }
}
