package arrow.meta.diagnostic;

import arrow.meta.plugins.proofs.phases.Proof;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory2;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.psi.KtObjectDeclaration;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.Collection;

import static arrow.meta.phases.analysis.diagnostic.PositionStrategiesKt.onIdentifyingElement;
import static arrow.meta.phases.analysis.diagnostic.PositionStrategiesKt.onPublishedInternalOrphan;
import static org.jetbrains.kotlin.diagnostics.Severity.ERROR;

public interface MetaErrors {
    // type proofs
    DiagnosticFactory0<KtDeclaration> PublishedInternalOrphan = DiagnosticFactory0.create(ERROR, onPublishedInternalOrphan);

    DiagnosticFactory2<KtDeclaration, Proof, Collection<? extends Proof>> AmbiguousProof = DiagnosticFactory2.create(ERROR);

    DiagnosticFactory1<KtDeclaration, Proof> OwnershipViolatedProof = DiagnosticFactory1.create(ERROR);

    DiagnosticFactory1<KtDeclaration, KotlinType> UnresolvedGivenProof = DiagnosticFactory1.create(ERROR);

    DiagnosticFactory2<KtExpression, ResolvedCall<?>, KotlinType> UnresolvedGivenCallSite = DiagnosticFactory2.create(ERROR);

    DiagnosticFactory1<KtElement, String> RefinementValidationError = DiagnosticFactory1.create(ERROR);

    DiagnosticFactory2<KtObjectDeclaration, KotlinType, KotlinType> IncorrectRefinement = DiagnosticFactory2.create(ERROR, onIdentifyingElement);

    DiagnosticFactory2<KtObjectDeclaration, KotlinType, KotlinType> TooManyRefinements = DiagnosticFactory2.create(ERROR, onIdentifyingElement);
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
