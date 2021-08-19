package arrow.meta.plugins.proofs.phases.resolve.cache

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.asProof
import arrow.meta.plugins.proofs.phases.isProof
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType

fun CompilerContext.cachedModule(name: Name): ModuleDescriptor? =
  proofCache.keys.firstOrNull { it.name == name }

fun ModuleDescriptor.initializeProofCache(ctx: CompilerContext): List<Proof> =
  try {
    val moduleProofs: List<Proof> = computeProofs(emptyList(), listOf(FqName.ROOT), skipPackages)
    ctx.run {
      if (moduleProofs.isNotEmpty()) { // remove old cached modules if this the same kind and has more recent proofs
        cachedModule(name)?.let { proofCache.remove(it) }
        proofCache[this@initializeProofCache] = ProofsCache(moduleProofs)
      }
    }
    moduleProofs
  } catch (e: Throwable) {
    emptyList()
  }

val skipPackages = setOf(
  FqName("com.apple"),
  FqName("com.oracle"),
  FqName("org.omg"),
  FqName("com.sun"),
  FqName("META-INF"),
  FqName("jdk"),
  FqName("apple"),
  FqName("java"),
  FqName("javax"),
  FqName("kotlin"),
  FqName("sun")
)

tailrec fun ModuleDescriptor.computeProofs(
  acc: List<Proof>,
  packages: List<FqName>,
  skipPacks: Set<FqName>
): List<Proof> =
  when {
    packages.isEmpty() -> acc
    else -> {
      val current = packages.first()
      val packagedProofs = getPackage(current).memberScope.getContributedDescriptors { true }
        .flatMap {
          if (it.isProof()) it.asProof()
          else emptySequence()
        }
      val remaining = (getSubPackagesOf(current) { true } + packages.drop(1)).filter { it !in skipPacks }
      val newAc = acc + packagedProofs
      val newRemainings = remaining
      computeProofs(
        acc = newAc,
        packages = newRemainings,
        skipPacks = skipPackages
      )
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
    }
  }
