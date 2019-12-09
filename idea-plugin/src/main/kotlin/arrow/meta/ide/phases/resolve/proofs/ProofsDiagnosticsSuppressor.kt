package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor

class ProofsDiagnosticsSuppressor : DiagnosticSuppressor {
  override fun isSuppressed(diagnostic: Diagnostic): Boolean = false
}
