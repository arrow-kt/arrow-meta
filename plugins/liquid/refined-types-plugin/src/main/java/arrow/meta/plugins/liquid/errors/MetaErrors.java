package arrow.meta.plugins.liquid.errors;

import arrow.meta.plugins.liquid.phases.analysis.solver.NamedConstraint;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory2;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;
import org.sosy_lab.java_smt.api.Formula;

import java.util.List;

import static org.jetbrains.kotlin.diagnostics.Severity.ERROR;

public interface MetaErrors {
    // type proofs
    DiagnosticFactory2<PsiElement, KtDeclaration, List<Formula>> InconsistentBodyPre = DiagnosticFactory2.create(ERROR);
    DiagnosticFactory2<PsiElement, KtDeclaration, List<NamedConstraint>> UnsatBodyPost = DiagnosticFactory2.create(ERROR);
    DiagnosticFactory2<PsiElement, ResolvedCall<?>, List<NamedConstraint>> UnsatCallPre = DiagnosticFactory2.create(ERROR);
    DiagnosticFactory2<PsiElement, ResolvedCall<?>, List<Formula>> InconsistentCallPost = DiagnosticFactory2.create(ERROR);
    DiagnosticFactory1<PsiElement, List<Formula>> InconsistentConditions = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, List<Formula>> InconsistentInvariants = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, List<NamedConstraint>> UnsatInvariants = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory0<PsiElement> ErrorParsingPredicate = DiagnosticFactory0.create(ERROR);

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
