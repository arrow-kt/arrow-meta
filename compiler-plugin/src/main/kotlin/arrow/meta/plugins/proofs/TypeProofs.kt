package arrow.meta.plugins.proofs

/*
Type Proofs are injective proofs from A -> B
Talk about Curry Howard Correspondence
 */

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.resolve.`isSubtypeOf(NewKotlinTypeChecker)`
import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.proofs.ProofTypeChecker
import arrow.meta.proofs.ProofsPackageFragmentDescriptor
import arrow.meta.proofs.chainedMemberScope
import arrow.meta.proofs.extensions
import arrow.meta.proofs.suppressConstantExpectedTypeMismatch
import arrow.meta.proofs.suppressExtensionUnresolvedReference
import arrow.meta.proofs.suppressProvenTypeMismatch
import arrow.meta.proofs.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.proofs.suppressUpperboundViolated
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PackageFragmentProviderImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      meta(
        enableIr(),
        packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
          object : PackageFragmentProvider {

            override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
              listOf(ProofsPackageFragmentDescriptor(module, fqName) { module.typeProofs })

            override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
              getPackageFragments(fqName).asSequence()
                .map { it.fqName }
                .filter { !it.isRoot && it.parent() == fqName }
                .toList()
          }
        },
//        syntheticScopes(
//          syntheticMemberFunctionsForName = { types, name, location ->
//            Log.Verbose({ "syntheticScopes.syntheticMemberFunctionsForName $types $name $this" }) {
//              scopeFunctions().filter { fn ->
//                types.any {
//                  name == fn.name &&
//                  it.`isSubtypeOf(NewKotlinTypeChecker)`(fn.extensionReceiverParameter?.type!!)
//                }
//              }
//            }
//          },
//          syntheticMemberFunctions = { types ->
//            Log.Verbose({ "syntheticScopes.syntheticMemberFunctions $types $this" }) {
//              scopeFunctions().filter { fn ->
//                types.any {
//                  it.`isSubtypeOf(NewKotlinTypeChecker)`(fn.extensionReceiverParameter?.type!!)
//                }
//              }
//            }
//          }
//        ),
//        packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
//
//          if (project is MockProject) {
//
//          }
//          val synth: JavaSyntheticScopes? = componentProvider?.get()
//          if (synth != null) {
//            val scopes = synth.scopes
//            if (scopes is MutableList) {
//              scopes.removeIf { it is ProofsSyntheticScope }
//              scopes.add(ProofsSyntheticScope { module.typeProofs })
//            }
//          }
//          null
//        },
//        storageComponent(
//          registerModuleComponents = { container, moduleDescriptor ->
//            val storage: ComponentStorage = StorageComponentContainer::class.java.getDeclaredField("componentStorage").also { it.isAccessible = true }.get(container) as ComponentStorage
//            val registry = ComponentStorage::class.java.getDeclaredField("descriptors").also { it.isAccessible = true }.get(storage) as LinkedHashSet<ValueDescriptor>
//            val singletons = registry.filterIsInstance<SingletonTypeComponentDescriptor>()
//            val componentDescriptor = singletons.find { it.klass == JavaSyntheticScopes::class.java }
//            componentDescriptor?.let { singleton ->
//              JavaSyntheticScopes()
//            }
//            val instance = component?.value as? JavaSyntheticScopes
//            if (instance != null) {
//              registrationMap.remove(classKey)
//              container.useInstance(ProofsSyntheticScopes(instance) { moduleDescriptor.typeProofs })
//            }
//          },
//          check = { declaration, descriptor, context ->
//
//          }
//        ),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            module.initializeProofCache()
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            null
          }
        ),
//          analysisCompleted = { project, module, bindingTrace, files ->
//            //            val calls = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
////            module.typeProofs.forEach {
////              calls.forEach { ktElement, call ->
////                val resolvedCall = call.getResolvedCall(bindingTrace.bindingContext)
////                val callReturnType = resolvedCall?.getReturnType()
////                if (callReturnType != null && !callReturnType.isNothing() && !callReturnType.isError && callReturnType.`isSubtypeOf(NewKotlinTypeChecker)`(it.from)) {
////                  if (ktElement is KtExpression) {
////                    val intersection = it.to.intersection(callReturnType)
////                    println("Smart cast $call for ${ktElement.text} with $callReturnType type: $intersection")
////                    bindingTrace.applySmartCast(call, ktElement, intersection) //TODO apply this in the synth resolution instead as the type classes plugin does and same for the IDE
////                  }
////                }
////              }
////            }
//            null
//          }
//        ),
        suppressDiagnostic { this.suppressProvenTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { this.suppressConstantExpectedTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { this.suppressTypeInferenceExpectedTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { it.suppressUpperboundViolated(module.typeProofs) },
        typeChecker { ProofTypeChecker(this) },
        irVariable { insertProof(module.typeProofs, it) },
        irProperty { insertProof(module.typeProofs, it) },
        irReturn { insertProof(module.typeProofs, it) },
        irDump()
      )
    }

private fun CompilerContext.scopeFunctions(): List<FunctionDescriptor> =
  { module.typeProofs }
    .chainedMemberScope()
    .getContributedDescriptors { true }
    .filterIsInstance<FunctionDescriptor>()

fun KtParameter.isNullable(): Boolean =
  typeReference?.typeElement is KtNullableType
