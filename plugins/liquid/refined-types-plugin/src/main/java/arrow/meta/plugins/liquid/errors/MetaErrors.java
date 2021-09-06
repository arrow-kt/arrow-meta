package arrow.meta.plugins.liquid.errors;

import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1;
import org.jetbrains.kotlin.diagnostics.Errors;

import static org.jetbrains.kotlin.diagnostics.Severity.ERROR;

public interface MetaErrors {
    // type proofs
    DiagnosticFactory1<PsiElement, String> InconsistentBodyPre = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> UnsatBodyPost = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> UnsatCallPre = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> InconsistentCallPost = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> InconsistentConditions = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> InconsistentInvariants = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> UnsatInvariants = DiagnosticFactory1.create(ERROR);
    DiagnosticFactory1<PsiElement, String> ErrorParsingPredicate = DiagnosticFactory1.create(ERROR);

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
