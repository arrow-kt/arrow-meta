package arrow.meta.diagnostic;

import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.KtDeclaration;

import static arrow.meta.phases.analysis.diagnostic.PositionStrategiesKt.onPublishedInternalOrphan;
import static org.jetbrains.kotlin.diagnostics.Severity.ERROR;

public interface MetaErrors {
    DiagnosticFactory0<KtDeclaration> PublishedInternalOrphan = DiagnosticFactory0.create(ERROR, onPublishedInternalOrphan);

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
