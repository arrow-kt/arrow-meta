package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.ide.phases.resolve.toSynthetic
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.extensionCallables
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.synthetic.SyntheticScopeProviderExtension
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult

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
    null

  override fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> =
    emptyList()

  override fun getSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    emptyList()

  override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
    emptyList()

  override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
    emptyList()

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions($receiverTypes), result: $this" }) {
      module.typeProofs.extensions(receiverTypes).flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension }
          .toSynthetic()
      }
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticMemberFunctions($receiverTypes, $name, ${location.location}), result: $this" }) {
      module.typeProofs.extensions(receiverTypes).flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension && it.name == name }
          .toSynthetic()
      }
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope), result: $this" }) {
      module.typeProofs.extensions().flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { !it.isExtension }
          .toSynthetic()
      }
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope, $name, ${location.location}), result: $this" }) {
      module.typeProofs.extensions().flatMap { proof ->
        proof.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { !it.isExtension && it.name == name }
          .toSynthetic()
      }
    }

}