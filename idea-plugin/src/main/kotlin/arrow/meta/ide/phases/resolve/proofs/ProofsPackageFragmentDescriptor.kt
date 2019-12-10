package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.toSynthetic
import arrow.meta.proofs.Proof
import arrow.meta.proofs.extensionCallables
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.addToStdlib.safeAs


fun List<Proof>.chainedMemberScope(): MemberScope {
  val synthProofs = flatMap {
    it.extensionCallables { true }
      .filterIsInstance<SimpleFunctionDescriptor>()
      .filter { it.isExtension }
      .mapNotNull {
        it.newCopyBuilder()
          .setModality(Modality.FINAL)
          .build()
      }
      .toSynthetic()
  }

  return object : MemberScope {
    override fun getClassifierNames(): Set<Name>? = synthProofs.map { it.name }.toSet()

    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
      Log.Verbose({ "ProofsPackageFragmentDescriptor.getContributedClassifier: $name $location $this" }) {
        synthProofs.firstOrNull { it.name == name }.safeAs()
      }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
      Log.Verbose({ "ProofsPackageFragmentDescriptor.getContributedDescriptors: $kindFilter $nameFilter $this" }) {
        synthProofs.filter { nameFilter(it.name) }
      }

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
      Log.Verbose({ "ProofsPackageFragmentDescriptor.getContributedFunctions: $name $location $this" }) {
        synthProofs.filter { it.name == name }
      }

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
      Log.Verbose({ "ProofsPackageFragmentDescriptor.getContributedVariables: $name $location $this" }) {
        emptyList()
      }

    override fun getFunctionNames(): Set<Name> =
      Log.Verbose({ "ProofsPackageFragmentDescriptor.getFunctionNames: $this" }) {
        synthProofs.map { it.name }.toSet()
      }

    override fun getVariableNames(): Set<Name> =
      Log.Verbose({ "ProofsPackageFragmentDescriptor.getVariableNames: $this" }) {
        emptySet()
      }

    override fun printScopeStructure(p: Printer) {
      println("printScopeStructure")
    }
  }
}