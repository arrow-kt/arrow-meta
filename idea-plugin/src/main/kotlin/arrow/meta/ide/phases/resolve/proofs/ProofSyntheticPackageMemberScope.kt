package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.resolve.toSynthetic
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope

class ProofSyntheticPackageMemberScope(val delegate: MemberScope): MemberScope by delegate {

  override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
    Log.Verbose({ "ProofSyntheticPackageMemberScope.getContributedDescriptors result: $this" }) {
      delegate.getContributedDescriptors(kindFilter, nameFilter)
        .filterIsInstance<SimpleFunctionDescriptor>()
        .toSynthetic()
    }

  override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
    Log.Verbose({ "ProofSyntheticPackageMemberScope.getContributedFunctions result: $this" }) {
      delegate.getContributedFunctions(name, location)
        .filterIsInstance<SimpleFunctionDescriptor>()
        .toSynthetic()
    }
}