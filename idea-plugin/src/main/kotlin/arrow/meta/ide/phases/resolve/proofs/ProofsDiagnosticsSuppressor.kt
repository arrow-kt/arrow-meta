package arrow.meta.ide.phases.resolve.proofs

import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor

class ProofsDiagnosticsSuppressor : DiagnosticSuppressor {
  override fun isSuppressed(diagnostic: Diagnostic): Boolean = false
}
