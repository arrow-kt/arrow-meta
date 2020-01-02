package arrow.meta.plugins.proofs.phases.resolve.scopes

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.callables
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal class ProofsMemberScope(private val synthProofs: () -> List<SimpleFunctionDescriptor>) : MemberScope {
  override fun getClassifierNames(): Set<Name>? = synthProofs().map { it.name }.toSet()

  override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
    Log.Silent({ "ProofsPackageFragmentDescriptor.getContributedClassifier: $name $location $this" }) {
      synthProofs().firstOrNull { it.name == name }.safeAs()
    }

  override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
    Log.Silent({ "ProofsPackageFragmentDescriptor.getContributedDescriptors: $kindFilter $nameFilter $this" }) {
      synthProofs().filter { nameFilter(it.name) }
    }

  override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
    Log.Silent({ "ProofsPackageFragmentDescriptor.getContributedFunctions: $name $location $this" }) {
      synthProofs().filter { it.name == name }
    }

  override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
    Log.Silent({ "ProofsPackageFragmentDescriptor.getContributedVariables: $name $location $this" }) {
      emptyList()
    }

  override fun getFunctionNames(): Set<Name> =
    Log.Silent({ "ProofsPackageFragmentDescriptor.getFunctionNames: $this" }) {
      synthProofs().map { it.name }.toSet()
    }

  override fun getVariableNames(): Set<Name> =
    Log.Silent({ "ProofsPackageFragmentDescriptor.getVariableNames: $this" }) {
      emptySet()
    }

  override fun printScopeStructure(p: Printer) {
    println("printScopeStructure")
  }
}

fun (() -> List<Proof>).memberScope(): MemberScope {
  val synthProofs by lazy {
    this().flatMap { proof ->
      proof.callables { true }
        .filterIsInstance<SimpleFunctionDescriptor>()
    }
  }
  return ProofsMemberScope { synthProofs }
}