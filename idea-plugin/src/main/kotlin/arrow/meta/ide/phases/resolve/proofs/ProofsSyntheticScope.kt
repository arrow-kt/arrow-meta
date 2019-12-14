package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.Proof
import arrow.meta.proofs.extensionCallables
import arrow.meta.proofs.extensions
import arrow.meta.proofs.syntheticMemberFunction
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
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

fun <A> withReadAccess(f: () -> A): A? =
  if (ApplicationManager.getApplication().isReadAccessAllowed) f()
  else null

operator fun <A> Log.invoke(
  tag: A.() -> String,
  f: () -> A
): A {
  val (time, result) = measureTimeMillisWithResult(f)
  if (this is Log.Verbose) {
    println("${tag(result)} : [${time}ms]: $result")
  }
  return result
}

class ProofsSyntheticScope(val module: ModuleDescriptor) : SyntheticScope {
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
    Log.Silent({ "ProofsSyntheticScope.getSyntheticMemberFunctions" }) {
      withReadAccess {
        module.typeProofs.extensions(receiverTypes).flatMap { proof ->
          proof.extensionCallables { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .filter { it.isExtension }
            .mapNotNull { fn ->
              val result = receiverTypes.first().constructor.declarationDescriptor?.safeAs<ClassDescriptor>()?.syntheticMemberFunction(fn)
              result
            }
        }
      }.orEmpty()
    }

  override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Silent({ "ProofsSyntheticScope.getSyntheticMemberFunctions" }) {
      withReadAccess {
        module.typeProofs.extensions(receiverTypes).flatMap { proof ->
          proof.extensionCallables { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .filter { it.isExtension && it.name == name }
            .mapNotNull { fn ->
              val result = receiverTypes.first().constructor.declarationDescriptor?.safeAs<ClassDescriptor>()?.syntheticMemberFunction(fn)
              result
            }
        }
      }.orEmpty()
    }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions($scope)" }) {
      withReadAccess {
        module.typeProofs.extensions().flatMap { proof ->
          proof.extensionCallables { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .filter { !it.isExtension }
            .mapNotNull {
              it.toStaticSynthetic(scope, proof)
            }
        }
      }.orEmpty()
    }

  private fun SimpleFunctionDescriptor.toStaticSynthetic(scope: ResolutionScope, proof: Proof): SimpleFunctionDescriptor? {
    val result = if (scope.getContributedFunctions(name, NoLookupLocation.FROM_BACKEND).isEmpty())
      staticSyntheticFunction(proof)
    else null
    return result
  }

  override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({ "ProofsSyntheticScope.getSyntheticStaticFunctions name: $name" }) {
      withReadAccess {
        module.typeProofs.extensions().flatMap { proof ->
          proof.extensionCallables { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .filter { !it.isExtension && it.name == name }
            .mapNotNull {
              it.toStaticSynthetic(scope, proof)
            }
        }
      }.orEmpty()
    }

}