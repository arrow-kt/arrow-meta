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
import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.proofs.MetaFileScopeProvider
import arrow.meta.proofs.ProofTypeChecker
import arrow.meta.proofs.ProofsBodyResolveContent
import arrow.meta.proofs.ProofsPackageFragmentDescriptor
import arrow.meta.proofs.suppressConstantExpectedTypeMismatch
import arrow.meta.proofs.suppressProvenTypeMismatch
import arrow.meta.proofs.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.proofs.suppressUpperboundViolated
import arrow.meta.proofs.syntheticMemberFunctions
import org.jetbrains.kotlin.analyzer.common.CommonPlatformAnalyzerServices
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.SimpleGlobalContext
import org.jetbrains.kotlin.context.withModule
import org.jetbrains.kotlin.context.withProject
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.frontend.di.createContainerForBodyResolve
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BodyResolver
import org.jetbrains.kotlin.resolve.LazyTopDownAnalyzer
import org.jetbrains.kotlin.resolve.ModuleStructureOracle
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.FlexibleTypeImpl
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.asSimpleType

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      meta(
        enableIr(),
//        analysis(
//          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
//            Log.Verbose({ "analysis.doAnalysis" }) {
//              val resolveSession: ResolveSession = componentProvider.get()
//              val lazyTopDownAnalyzer: LazyTopDownAnalyzer = componentProvider.get()
//              files.forEach {
//                resolveBodyWithExtensionsScope(resolveSession, lazyTopDownAnalyzer, it)
//              }
//              null
//            }
//            //resolveBodyWithExtensionsScope
//          },
//          analysisCompleted = { project, module, bindingTrace, files ->
//            null
//          }
//        ),
        packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
          object : PackageFragmentProvider {

            override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
              Log.Verbose({ "packageFragmentProvider.getPackageFragments $fqName" }) {
                listOf(ProofsPackageFragmentDescriptor(module, fqName) { module.typeProofs })
              }


            override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
              Log.Verbose({ "packageFragmentProvider.getSubPackagesOf $fqName" }) {
                emptyList()
              }
          }
        },
        syntheticResolver(
          addSyntheticSupertypes = { thisDescriptor, supertypes ->
            Log.Verbose({ "syntheticResolver.addSyntheticSupertypes $thisDescriptor, $supertypes" }) {

            }
          },
          generatePackageSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
            Log.Verbose({ "syntheticResolver.generatePackageSyntheticClasses $thisDescriptor, $name" }) {
            }
          },
          generateSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
            Log.Verbose({ "syntheticResolver.generateSyntheticClasses $thisDescriptor, $name" }) {

            }
          },
          generateSyntheticMethods = { thisDescriptor, name, ctx, fromSuperTypes, result ->
            Log.Verbose({ "syntheticResolver.generateSyntheticMethods $thisDescriptor, $name, $fromSuperTypes" }) {

            }
          },
          generateSyntheticProperties = { thisDescriptor, name, ctx, fromSuperTypes, result ->
            Log.Verbose({ "syntheticResolver.generateSyntheticProperties $thisDescriptor, $name, $fromSuperTypes" }) {

            }
          },
          getSyntheticNestedClassNames = { thisDescriptor ->
            Log.Verbose({ "syntheticResolver.getSyntheticNestedClassNames $thisDescriptor" }) {
              emptyList()
            }
          },
          getSyntheticCompanionObjectNameIfNeeded = { thisDescriptor ->
            Log.Verbose({ "syntheticResolver.getSyntheticCompanionObjectNameIfNeeded $thisDescriptor" }) {
              null
            }
          },
          getSyntheticFunctionNames = { thisDescriptor ->
            Log.Verbose({ "syntheticResolver.getSyntheticFunctionNames $thisDescriptor" }) {
              emptyList()
            }
          }
        ),
//        packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
//          object : PackageFragmentProvider {
//
//            override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
//              listOf(ProofsPackageFragmentDescriptor(module, fqName) { module.typeProofs })
//
//            override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
//              emptySet()
//          }
//        },
//        suppressDiagnostic {
//          false
//        },
        syntheticScopes(
          syntheticMemberFunctionsForName = { types, name, location ->
            Log.Verbose({ "syntheticScopes.syntheticMemberFunctionsForName $types $name $this" }) {
              val proofs = module?.typeProofs.orEmpty()
              proofs.syntheticMemberFunctions(types, name)
            }
          },
          syntheticMemberFunctions = { types ->
            Log.Verbose({ "syntheticScopes.syntheticMemberFunctions $types $this" }) {
              module?.typeProofs?.syntheticMemberFunctions(types).orEmpty()
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
        ),
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
            val argumentTypeResolver: ArgumentTypeResolver = componentProvider.get()
            val typeCheckerField = ArgumentTypeResolver::class.java.getDeclaredField("kotlinTypeChecker").also { it.isAccessible = true }
            typeCheckerField.set(argumentTypeResolver, ProofTypeChecker(this))
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
//            val typeInfoMap: ImmutableMap<KtExpression, KotlinTypeInfo> = bindingTrace.bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO)
//            typeInfoMap.entries.forEach { (expression, typeInfo) ->
//              val smartCasts = typeInfo.type?.let { expressionType ->
//                module.typeProofs.extensions(expressionType).mapNotNull {
//                  it.extensionCallables { true }.firstOrNull()?.dispatchReceiverParameter?.type
//                }
//              }.orEmpty()
//              val newKotlinTypeInfo = typeInfo?.type?.let {
//                typeInfo.replaceDataFlowInfo(
//                  subtyping(it, module, bindingTrace.bindingContext, typeInfo.dataFlowInfo, smartCasts)
//                )
//              }
//              newKotlinTypeInfo?.also {
//                bindingTrace.record(BindingContext.EXPRESSION_TYPE_INFO, expression, it)
//              }
//            }
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
//        irDump(),
        irCall { insertCallProofs(module.typeProofs, it) },
        irProperty { insertProof(module.typeProofs, it) },
        irVariable { insertProof(module.typeProofs, it) },
        irReturn { insertProof(module.typeProofs, it) },
        irDump()
      )
    }

fun KtParameter.isNullable(): Boolean =
  typeReference?.typeElement is KtNullableType


fun createBodyResolver(
  resolveSession: ResolveSession,
  trace: BindingTrace,
  file: KtFile,
  statementFilter: StatementFilter
): BodyResolver {
  val globalContext = SimpleGlobalContext(resolveSession.storageManager, resolveSession.exceptionTracker)
  val module = resolveSession.moduleDescriptor
  return createContainerForBodyResolve(
    globalContext.withProject(file.project).withModule(module),
    trace,
    CommonPlatforms.defaultCommonPlatform,
    statementFilter,
    CommonPlatformAnalyzerServices,
    LanguageVersionSettingsImpl.DEFAULT,
    ModuleStructureOracle.SingleModule
  ).get()
}

fun resolveBodyWithExtensionsScope(session: ResolveSession, lazyTopDownAnalyzer: LazyTopDownAnalyzer, ktFile: KtFile): Unit {
  Log.Verbose({ "resolveBodyWithExtensionsScope $ktFile" }) {
    session.fileScopeProvider = MetaFileScopeProvider(session.moduleDescriptor, session.fileScopeProvider)
    val bodyResolver = createBodyResolver(
      session, session.trace, ktFile, StatementFilter.NONE
    )
    val analysisContext = lazyTopDownAnalyzer.analyzeDeclarations(TopDownAnalysisMode.TopLevelDeclarations, listOf(ktFile), DataFlowInfo.EMPTY)
    val resolveContext = ProofsBodyResolveContent(
      session = session,
      delegate = analysisContext
    )
    bodyResolver.resolveBodies(resolveContext)
  }
}

fun CompilerContext.subtyping(
  originalType: KotlinType,
  module: ModuleDescriptor,
  bindingContext: BindingContext,
  dataFlowInfo: DataFlowInfo,
  types: Collection<KotlinType>
): DataFlowInfo {
  val dataFlowValueFactory: DataFlowValueFactory? = componentProvider?.get()
  return dataFlowValueFactory?.let { factory ->
    val result = types.fold(dataFlowInfo) { info, type ->
      val value = factory.createDataFlowValue(TransientReceiver(FlexibleTypeImpl(originalType.asSimpleType(), originalType.asSimpleType())), bindingContext, module)
      info.establishSubtyping(value, type, LanguageVersionSettingsImpl.DEFAULT)
    }
    result
  } ?: dataFlowInfo
}