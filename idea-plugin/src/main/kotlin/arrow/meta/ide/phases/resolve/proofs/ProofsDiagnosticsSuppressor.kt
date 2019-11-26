package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor

class ProofsDiagnosticsSuppressor : DiagnosticSuppressor {
  override fun isSuppressed(diagnostic: Diagnostic): Boolean =
    Log.Verbose({ "ProofsDiagnosticsSuppressor.isSuppressed(${diagnostic.factory}), result: $this" }) {
      if (diagnostic.factory == Errors.UNRESOLVED_REFERENCE) {
        Errors.UNRESOLVED_REFERENCE.cast(diagnostic).let {
          val module = it.a.findModuleDescriptor()
          module.initializeProofCache()
          val shouldSuppress = module.typeProofs.extensions().any { ext ->
            ext.to.memberScope.getContributedDescriptors { true }.any {
              it.name.asString() == diagnostic.psiElement.text
            }
          }
          Log.Verbose({ "ProofsDiagnosticsSuppressor.shouldSuppress(${diagnostic.factory}), result: $this" }) {
            shouldSuppress
          }
        }
      } else false
    }
}