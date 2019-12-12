package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.extensionCallables
import arrow.meta.proofs.extensions
import arrow.meta.proofs.syntheticMemberFunction
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.resolve.scopes.receivers.CastImplicitClassReceiver
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.synthetic.SyntheticScopeProviderExtension
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class MetaSyntheticScope : SyntheticScopeProviderExtension {
  override fun getScopes(moduleDescriptor: ModuleDescriptor, javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope): List<SyntheticScope> =
   listOf(ProofsSyntheticScope(moduleDescriptor))
}

sealed class Log {
  object Silent : Log()
  object Verbose : Log()
}

operator fun <A> Log.invoke(tag: A.() -> String, f: () -> A): A {
  val (time, result) = measureTimeMillisWithResult(f)
  if (this is Log.Verbose) {
    println("${tag(result)} : [${time}ms]: $result")
  }
  return result
}

class ProofsSyntheticScope(val module: ModuleDescriptor): SyntheticScope {
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
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions" }) {
      module.typeProofs.extensions(receiverTypes).flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension }
          .mapNotNull { fn ->
            receiverTypes.first().constructor.declarationDescriptor?.safeAs<ClassDescriptor>()?.syntheticMemberFunction(fn)
          }
      }
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions" }) {
      module.typeProofs.extensions(receiverTypes).flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension && it.name == name }
          .mapNotNull { fn ->
            receiverTypes.first().constructor.declarationDescriptor?.safeAs<ClassDescriptor>()?.syntheticMemberFunction(fn)
          }
      }
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope)" }) {
      module.typeProofs.extensions().flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { !it.isExtension }
      }
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions" }) {
      module.typeProofs.extensions().flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { !it.isExtension && it.name == name }
      }
    }

}