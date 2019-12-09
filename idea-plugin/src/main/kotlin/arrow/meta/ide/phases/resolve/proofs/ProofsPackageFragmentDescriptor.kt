package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.proofCache
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.Proof
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.scopes.ChainedMemberScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope

fun MemberScope?.orEmpty(): MemberScope =
  this ?: MemberScope.Empty

class ProofsPackageFragmentDescriptor(
  val module: ModuleDescriptor,
  fqName: FqName
) : PackageFragmentDescriptorImpl(module, fqName) {
  override fun getMemberScope(): MemberScope =
    Log.Verbose({ "ProofsPackageFragmentDescriptor.getMemberScope $this" }) {
      if (proofCache.keys.any { it == module.name })
        module.typeProofs.chainedMemberScope().orEmpty()
      else MemberScope.Empty
    }
}

fun List<Proof>.chainedMemberScope(): ChainedMemberScope =
  ChainedMemberScope(
    debugName = "ExtensionsScope",
    scopes = extensions().map { ProofSyntheticPackageMemberScope(it.to.memberScope) }
  )