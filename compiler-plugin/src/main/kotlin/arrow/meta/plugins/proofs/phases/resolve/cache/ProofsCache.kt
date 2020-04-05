package arrow.meta.plugins.proofs.phases.resolve.cache

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.callables
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.resolve.asProof
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import java.util.concurrent.ConcurrentHashMap

data class ProofsCache(
  val proofs: List<Proof>
)

val proofCache: ConcurrentHashMap<ModuleDescriptor, ProofsCache> = ConcurrentHashMap()

fun disposeProofCache(): Unit =
  proofCache.clear()

fun cachedModule(name: Name): ModuleDescriptor? =
  proofCache.keys.firstOrNull { it.name == name }

fun ModuleDescriptor.initializeProofCache(): List<Proof> =
  try {
    val moduleProofs: List<Proof> = computeModuleProofs()
    if (moduleProofs.isNotEmpty()) { //remove old cached modules if this the same kind and has more recent proofs
      cachedModule(name)?.let { proofCache.remove(it) }
      proofCache[this] = ProofsCache(moduleProofs)
    }
    moduleProofs
  } catch (e: Throwable) {
    Log.Verbose({ "initializeProofCache found error $e" }) {}
    emptyList()
  }

private fun ModuleDescriptor.computeModuleProofs(): List<Proof> =
  Log.Verbose({ "Recomputed cache proofs: ${size}, module cache size: ${proofCache.size}, \n ${show()}" }) {
    (getSubPackagesOf(FqName.ROOT) { true })
      .filter { !it.isRoot }
      .flatMap { packageName ->
        getPackage(packageName).memberScope.getContributedDescriptors { true }
          .filterIsInstance<FunctionDescriptor>()
          .filter(FunctionDescriptor::isProof)
          .mapNotNull(FunctionDescriptor::asProof)
      }
  }

private fun KotlinType.show(length: Int): String {
  val display = toString()
  return if (display.length <= length) display
  else "${display.substring(0, length)}..."
}

private fun List<Proof>.show(): String =
  joinToString("\n") {
    "${it.from.show(20)} -> ${it.proofType.name} -> ${it.to.show(20)}"
  }