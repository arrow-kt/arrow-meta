package arrow.meta.diagnostic;

import arrow.meta.plugins.proofs.phases.ExtensionProof;
import arrow.meta.plugins.proofs.phases.Proof;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory2;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.Collection;

import static arrow.meta.phases.analysis.diagnostic.PositionStrategiesKt.onNavigationElement;
import static arrow.meta.phases.analysis.diagnostic.PositionStrategiesKt.onPublishedInternalOrphan;
import static org.jetbrains.kotlin.diagnostics.Severity.ERROR;

public interface MetaErrors {
    // type proofs
    DiagnosticFactory0<KtDeclaration> PublishedInternalOrphan = DiagnosticFactory0.create(ERROR, onPublishedInternalOrphan);

    DiagnosticFactory2<KtNamedFunction, ExtensionProof, Collection<ExtensionProof>> AmbiguousExtensionProof = DiagnosticFactory2.create(ERROR);

    DiagnosticFactory1<KtDeclaration, Proof> OwnershipViolatedProof = DiagnosticFactory1.create(ERROR);

    /**
     * needed to prevent NPE in
     * org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages#getRendererForDiagnostic(org.jetbrains.kotlin.diagnostics.Diagnostic)
     */
    @SuppressWarnings("UnusedDeclaration")
    Object _initializer = new Object() {
        {
            Errors.Initializer.initializeFactoryNames(MetaErrors.class);
        }
    };
}
