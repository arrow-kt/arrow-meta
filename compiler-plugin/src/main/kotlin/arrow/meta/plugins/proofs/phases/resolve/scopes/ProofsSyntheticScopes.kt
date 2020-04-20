package arrow.meta.plugins.proofs.phases.resolve.scopes

import arrow.meta.Meta
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.unwrappedNotNullableType
import arrow.meta.plugins.proofs.phases.callables
import arrow.meta.plugins.proofs.phases.extending
import arrow.meta.plugins.proofs.phases.ir.ProofCandidate
import arrow.meta.plugins.proofs.phases.ir.typeSubstitutor
import arrow.meta.plugins.proofs.phases.resolve.ProofReceiverValue
import org.jetbrains.kotlin.codegen.coroutines.createCustomCopy
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.inference.substitute
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name): List<SimpleFunctionDescriptor> =
  syntheticMemberFunctions(receiverTypes)
    .filter { it.name == name }

fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): List<SimpleFunctionDescriptor> =
  extending(receiverTypes).flatMap { proof ->
    proof.callables { true }
      .filterIsInstance<SimpleFunctionDescriptor>()
      .filter { !it.isExtension }
      .flatMap {
        receiverTypes.map { receiverType ->
          val substitutor = ProofCandidate(
            proofType = proof.from,
            otherType = receiverType.unwrappedNotNullableType,
            through = it
          ).typeSubstitutor
          val targetType = substitutor.safeSubstitute(proof.to.unwrap())
          val receiver = ProofReceiverValue(targetType)
          val dispatchReceiver = ReceiverParameterDescriptorImpl(it, receiver, Annotations.EMPTY).substitute(substitutor) as ReceiverParameterDescriptorImpl
          val resultingFunction =
            it.substitute(substitutor).safeAs<SimpleFunctionDescriptor>()
              ?.createCustomCopy {
                setPreserveSourceElement()
                setDispatchReceiverParameter(dispatchReceiver).setDropOriginalInContainingParts()
                  .setOriginal(it)
              }
          val result = resultingFunction?.createCustomCopy {
            setPreserveSourceElement()
            setDispatchReceiverParameter(ReceiverParameterDescriptorImpl(resultingFunction, ProofReceiverValue(receiverType), Annotations.EMPTY))
          }
          result
        }.filterIsInstance<SimpleFunctionDescriptor>()
//        it.newCopyBuilder()
//          .setDropOriginalInContainingParts()
//          .setOriginal(it)
//          .setDispatchReceiverParameter(dispatchReceiver)
//          .build()
      }
  }

class ProofsSyntheticScope(private val ctx: CompilerContext) : SyntheticScope {
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
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions types: $receiverTypes $this" }) {
      ctx.syntheticMemberFunctions(receiverTypes)
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticMemberFunctions $this" }) {
      ctx.syntheticMemberFunctions(receiverTypes, name)
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope)" }) {
      emptyList()
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticStaticFunctions name: $name" }) {
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

fun Meta.provenSyntheticScope(): ExtensionPhase =
  syntheticScopes(
    syntheticMemberFunctionsForName = { types, name, _ ->
      Log.Silent({ "syntheticScopes.syntheticMemberFunctionsForName $types $name $this" }) {
        syntheticMemberFunctions(types, name)
      }
    },
    syntheticMemberFunctions = { types ->
      Log.Silent({ "syntheticScopes.syntheticMemberFunctions $types $this" }) {
        syntheticMemberFunctions(types)
      }
    },
    syntheticStaticFunctions = { scope ->
      Log.Silent({ "syntheticScopes.syntheticStaticFunctions $scope $this" }) {
        emptyList()
      }
    },
    syntheticStaticFunctionsForName = { scope, name, location ->
      Log.Silent({ "syntheticScopes.syntheticStaticFunctionsForName $scope $name $location $this" }) {
        emptyList()
      }
    },
    syntheticConstructor = { constructor ->
      Log.Silent({ "syntheticScopes.syntheticConstructor $constructor" }) {
        null
      }
    },
    syntheticConstructors = { scope ->
      Log.Silent({ "syntheticScopes.syntheticConstructors $scope" }) {
        emptyList()
      }
    },
    syntheticConstructorsForName = { scope, name, location ->
      Log.Silent({ "syntheticScopes.syntheticConstructorsForName $scope $name, $location" }) {
        emptyList()
      }
    },
    syntheticExtensionProperties = { receiverTypes, location ->
      Log.Silent({ "syntheticScopes.syntheticExtensionProperties $receiverTypes, $location" }) {
        emptyList()
      }
    },
    syntheticExtensionPropertiesForName = { receiverTypes, name, location ->
      Log.Silent({ "syntheticScopes.syntheticExtensionPropertiesForName $receiverTypes, $name, $location" }) {
        emptyList()
      }
    }
  )