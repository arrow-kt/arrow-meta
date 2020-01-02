package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.resolve.ProofsSyntheticScope
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.synthetic.SyntheticScopeProviderExtension

class MetaSyntheticScope : SyntheticScopeProviderExtension {
  override fun getScopes(moduleDescriptor: ModuleDescriptor, javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope): List<SyntheticScope> =
    withReadAccess {
      Log.Verbose({ "MetaSyntheticScope.getScopes" }) {
        listOf(ProofsSyntheticScope { moduleDescriptor.proofs })
      }
    }.orEmpty()
}

fun <A> withReadAccess(f: () -> A): A? =
  if (ApplicationManager.getApplication().isReadAccessAllowed) f()
  else null

