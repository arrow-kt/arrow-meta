package arrow.meta.plugins.proofs.phases.resolve.cache

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.callables
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.resolve.asProof
import arrow.meta.plugins.proofs.phases.resolve.scopes.synthetic
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.util.concurrent.ConcurrentHashMap

data class ProofsCache(
  val proofs: List<Proof>,
  val extensionCallables: List<CallableMemberDescriptor> = proofs.flatMap { it.callables { true } }
)

val proofCache: ConcurrentHashMap<ModuleDescriptor, ProofsCache> = ConcurrentHashMap()
fun disposeProofCache(): Unit =
  proofCache.clear()

fun cachedModule(name: Name): ModuleDescriptor? =
  proofCache.keys.firstOrNull { it.name == name }

internal fun ModuleDescriptor.initializeProofCache(): List<Proof> {
  val moduleProofs: List<Proof> = computeModuleProofs()
  if (moduleProofs.isNotEmpty()) { //remove old cached modules if this the same kind and has more recent proofs
    cachedModule(name)?.let { proofCache.remove(it) }
    proofCache[this] = ProofsCache(moduleProofs)
  }
  return moduleProofs
}

private fun ModuleDescriptor.computeModuleProofs(): List<Proof> =
  Log.Verbose({"Recomputed cache proofs: ${size}, module cache size: ${proofCache.size}"}) {
    (getSubPackagesOf(FqName.ROOT) { true })
      .filter { !it.isRoot }
      .flatMap { packageName ->
        getPackage(packageName).memberScope.getContributedDescriptors { true }
          .filterIsInstance<FunctionDescriptor>()
          .filter(FunctionDescriptor::isProof)
          .mapNotNull(FunctionDescriptor::asProof)
      }.synthetic()
  }