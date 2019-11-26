package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.proofs.Proof
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.scopes.ChainedMemberScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope

class ProofsPackageFragmentDescriptor(
  module: ModuleDescriptor,
  fqName: FqName,
  val proofs: List<Proof>
) : PackageFragmentDescriptorImpl(module, fqName) {
  override fun getMemberScope(): MemberScope =
    ChainedMemberScope(
      debugName = "ExtensionsScope",
      scopes = proofs.extensions().map { ProofSyntheticPackageMemberScope(it.to.memberScope) }
    )
}