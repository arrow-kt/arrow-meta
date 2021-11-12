package arrow.meta.proofs

import arrow.meta.proofs.generators.AllOpenMemberGenerator
import arrow.meta.proofs.generators.AllOpenNestedClassGenerator
import arrow.meta.proofs.generators.AllOpenRecursiveNestedClassGenerator
import arrow.meta.proofs.generators.AllOpenTopLevelDeclarationsGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class ProofsComponentRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::AllOpenStatusTransformer
    +::AllOpenVisibilityTransformer
    +::AllOpenSupertypeGenerator

    // Declaration generators
    +::AllOpenMemberGenerator
    +::AllOpenNestedClassGenerator
    +::AllOpenAdditionalCheckers
    +::AllOpenTopLevelDeclarationsGenerator
    +::AllOpenRecursiveNestedClassGenerator
  }
}
