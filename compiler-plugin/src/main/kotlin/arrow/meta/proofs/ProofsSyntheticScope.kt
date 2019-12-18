package arrow.meta.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class ProofsSyntheticScope(val proofs: () -> List<Proof>) : SyntheticScope {
  override fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticConstructor($constructor), result: $this" }) {
      null
    }

  override fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticConstructor($scope), result: $this" }) {
      emptyList()
    }

  override fun getSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticConstructors($scope), $name result: $this" }) {
      emptyList()
    }

  override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticExtensionProperties($receiverTypes) result: $this" }) {
      emptyList()
    }

  override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticExtensionProperties($receiverTypes, $name) result: $this" }) {
      emptyList()
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions [Proofs: ${proofs().size}] $this" }) {
      proofs().extensions(receiverTypes).flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension && it.extensionReceiverParameter?.type in receiverTypes }
          .mapNotNull { fn ->
            val result = receiverTypes.first().constructor.declarationDescriptor?.safeAs<ClassDescriptor>()?.syntheticMemberFunction(fn)
            result
          }
      }
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions [Proofs: ${proofs().size}] $this" }) {
      proofs().extensions(receiverTypes).flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension && it.name == name && it.extensionReceiverParameter?.type in receiverTypes }
          .mapNotNull { fn ->
            val result = receiverTypes.first().constructor.declarationDescriptor?.safeAs<ClassDescriptor>()?.syntheticMemberFunction(fn)
            result
          }
      }
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope)" }) {
      proofs().extensions().flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { !it.isExtension }
          .mapNotNull {
            it.toStaticSynthetic(scope, proof)
          }
      }
    }

  private fun SimpleFunctionDescriptor.toStaticSynthetic(scope: ResolutionScope, proof: Proof): SimpleFunctionDescriptor? {
    val result = if (scope.getContributedFunctions(name, NoLookupLocation.FROM_BACKEND).isEmpty())
      staticSyntheticFunction(proof)
    else null
    return result
  }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions name: $name" }) {
      proofs().extensions().flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { !it.isExtension && it.name == name }
          .mapNotNull {
            it.toStaticSynthetic(scope, proof)
          }
      }
    }
}