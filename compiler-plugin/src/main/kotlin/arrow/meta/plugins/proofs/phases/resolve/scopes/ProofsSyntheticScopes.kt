package arrow.meta.plugins.proofs.phases.resolve.scopes

import arrow.meta.Meta
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.callables
import arrow.meta.plugins.proofs.phases.extending
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.types.KotlinType

fun SimpleFunctionDescriptor.syntheticFunction2(
  containingDeclaration: DeclarationDescriptor?,
  extensionReceiver: ReceiverParameterDescriptor?,
  dispatchReceiver: ReceiverParameterDescriptor?,
  source: SourceElement
): SimpleFunctionDescriptor? {
  return SimpleFunctionDescriptorImpl.create(
    containingDeclaration ?: this.containingDeclaration,
    Annotations.EMPTY,
    name,
    CallableMemberDescriptor.Kind.DECLARATION,
    source
  ).initialize(
    extensionReceiver,
    dispatchReceiver,
    typeParameters,
    valueParameters,
    returnType,
    Modality.FINAL,
    Visibilities.PUBLIC
  ).newCopyBuilder()
    .setOriginal(this@syntheticFunction2)
    .build()
}

fun List<Proof>.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name): List<SimpleFunctionDescriptor> =
  syntheticMemberFunctions(receiverTypes)
    .filter { it.name == name }

fun List<Proof>.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): List<SimpleFunctionDescriptor> =
  extending(receiverTypes).flatMap { proof ->
    proof.callables { true }
      .filterIsInstance<SimpleFunctionDescriptor>()
  }

fun List<SimpleFunctionDescriptor>.toSynthetic(): List<SimpleFunctionDescriptor> =
  mapNotNull { it.synthetic() }

inline fun <reified C : CallableMemberDescriptor> C.synthetic(): C =
  copy(
    containingDeclaration,
    modality,
    if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    true
  ) as C

fun List<Proof>.synthetic(): List<Proof> =
  mapNotNull { proof ->
    Proof(proof.from, proof.to, (proof.through as SimpleFunctionDescriptor).synthetic(), proof.proofType)
  }

class ProofsSyntheticScope(val proofs: () -> List<Proof>) : SyntheticScope {
  override fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticConstructor($constructor), result: $this" }) {
      null
    }

  override fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticConstructor($scope), result: $this" }) {
      emptyList()
    }

  override fun getSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticConstructors($scope), $name result: $this" }) {
      emptyList()
    }

  override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticExtensionProperties($receiverTypes) result: $this" }) {
      emptyList()
    }

  override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticExtensionProperties($receiverTypes, $name) result: $this" }) {
      emptyList()
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions [Proofs: ${proofs().size}] $this" }) {
      proofs().syntheticMemberFunctions(receiverTypes)
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions [Proofs: ${proofs().size}] $this" }) {
      proofs().syntheticMemberFunctions(receiverTypes, name)
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope)" }) {
      emptyList()
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions name: $name" }) {
      emptyList()
    }
}

fun CallableMemberDescriptor.discardPlatformBaseObjectFakeOverrides(): CallableMemberDescriptor? =
  when (kind) {
    CallableMemberDescriptor.Kind.FAKE_OVERRIDE ->
      if (dispatchReceiverParameter?.type == builtIns.anyType) null
      else this
    else -> this
  }

fun Meta.registerProofSyntheticScope(): ExtensionPhase {
  return syntheticScopes(
    syntheticMemberFunctionsForName = { types, name, location ->
      Log.Verbose({ "syntheticScopes.syntheticMemberFunctionsForName $types $name $this" }) {
        val proofs = module?.proofs.orEmpty()
        proofs.syntheticMemberFunctions(types, name)
      }
    },
    syntheticMemberFunctions = { types ->
      Log.Verbose({ "syntheticScopes.syntheticMemberFunctions $types $this" }) {
        module?.proofs?.syntheticMemberFunctions(types).orEmpty()
      }
    },
    syntheticStaticFunctions = { scope ->
      Log.Verbose({ "syntheticScopes.syntheticStaticFunctions $scope $this" }) {
        emptyList()
      }
    },
    syntheticStaticFunctionsForName = { scope, name, location ->
      Log.Verbose({ "syntheticScopes.syntheticStaticFunctionsForName $scope $name $location $this" }) {
        emptyList()
      }
    },
    syntheticConstructor = { constructor ->
      Log.Verbose({ "syntheticScopes.syntheticConstructor $constructor" }) {
        null
      }
    },
    syntheticConstructors = { scope ->
      Log.Verbose({ "syntheticScopes.syntheticConstructors $scope" }) {
        emptyList()
      }
    },
    syntheticConstructorsForName = { scope, name, location ->
      Log.Verbose({ "syntheticScopes.syntheticConstructorsForName $scope $name, $location" }) {
        emptyList()
      }
    },
    syntheticExtensionProperties = { receiverTypes, location ->
      Log.Verbose({ "syntheticScopes.syntheticExtensionProperties $receiverTypes, $location" }) {
        emptyList()
      }
    },
    syntheticExtensionPropertiesForName = { receiverTypes, name, location ->
      Log.Verbose({ "syntheticScopes.syntheticExtensionPropertiesForName $receiverTypes, $name, $location" }) {
        emptyList()
      }
    }
  )
}