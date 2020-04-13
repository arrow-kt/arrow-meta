package arrow.meta.plugins.proofs.phases.resolve.cache

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.RefinementProof
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.resolve.asProof
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
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
    val moduleProofs: List<Proof> = computeModuleProofs(emptySequence(), listOf(FqName.ROOT)).toList()
    if (moduleProofs.isNotEmpty()) { //remove old cached modules if this the same kind and has more recent proofs
      cachedModule(name)?.let { proofCache.remove(it) }
      proofCache[this] = ProofsCache(moduleProofs)
    }
    Log.Verbose({ "initializeProofCache proofs: ${moduleProofs.size}" }) {}
    moduleProofs
  } catch (e: Throwable) {
    Log.Verbose({ "initializeProofCache found error $e" }) {}
    emptyList()
  }



private tailrec fun ModuleDescriptor.computeModuleProofs(
  acc: Sequence<Proof>,
  packages: List<FqName>
): Sequence<Proof> =
  when {
    packages.isEmpty() -> acc
    else -> {
      val current = packages.first()
      val remaining = packages.drop(1)
      val packagedProofs = (getSubPackagesOf(current) { true })
        .filter { !it.isRoot }
        .flatMap { packageName ->
          getPackage(packageName).memberScope.getContributedDescriptors { true }
            .filter { it.isProof() }
            .flatMap { it.asProof().asIterable() }
            .map { it to packageName }
        }.toMap()
      computeModuleProofs(acc + packagedProofs.keys.asSequence(), remaining)
    }
  }


private fun KotlinType.show(length: Int): String {
  val display = toString()
  return if (display.length <= length) display
  else "${display.substring(0, length)}..."
}

private fun List<Proof>.show(): String =
  joinToString("\n") {
    when (it) {
      is GivenProof -> "Given: -> ${it.to.show(20)}"
      is ExtensionProof -> "Extension: ${it.from.show(20)} -> ${it.to.show(20)}"
      is RefinementProof -> "Refinement: ${it.from.show(20)} -> ${it.to.show(20)}"
    }
  }