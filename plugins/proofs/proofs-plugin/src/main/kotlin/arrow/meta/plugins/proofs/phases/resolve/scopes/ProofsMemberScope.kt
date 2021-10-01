package arrow.meta.plugins.proofs.phases.resolve.scopes

import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.callables
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
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

internal class ProofsMemberScope(private val synthProofs: () -> List<CallableMemberDescriptor>) : MemberScope {
  override fun getClassifierNames(): Set<Name>? = synthProofs().map { it.name }.toSet()

  override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
    synthProofs().firstOrNull { it.name == name }.safeAs<ClassifierDescriptor>()

  override fun getContributedDescriptors(
    kindFilter: DescriptorKindFilter,
    nameFilter: (Name) -> Boolean
  ): Collection<DeclarationDescriptor> =
    synthProofs().filter { nameFilter(it.name) }

  override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
    synthProofs().filterIsInstance<SimpleFunctionDescriptor>().filter { it.name == name }

  override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
    emptyList()

  override fun getFunctionNames(): Set<Name> =
    synthProofs().map { it.name }.toSet()

  override fun getVariableNames(): Set<Name> =
    emptySet()

  override fun printScopeStructure(p: Printer) {
    println("printScopeStructure")
  }
}

fun (() -> List<Proof>).memberScope(): MemberScope {
  val synthProofs by lazy {
    this().flatMap { proof ->
      proof.callables { true }
    }
  }
  return ProofsMemberScope { synthProofs }
}
