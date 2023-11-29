package arrow.meta.internal.registry

import arrow.meta.CliPlugin
import arrow.meta.dsl.config.ConfigSyntax
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.AnalysisContext.canRewind
import arrow.meta.phases.analysis.AnalysisContext.popAnalysisPhase
import arrow.meta.phases.analysis.AnalysisContext.pushAnalysisPhase
import arrow.meta.phases.analysis.AnalysisContext.willRewind
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.phases.codegen.asm.ClassBuilder
import arrow.meta.phases.codegen.asm.ClassGeneration
import arrow.meta.phases.codegen.asm.Codegen
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.config.Config
import arrow.meta.phases.config.StorageComponentContainer
import arrow.meta.phases.resolve.DeclarationAttributeAlterer
import arrow.meta.phases.resolve.PackageProvider
import arrow.meta.phases.resolve.synthetics.SyntheticResolver
import arrow.meta.phases.resolve.synthetics.SyntheticScopeProvider
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.extensions.CollectAdditionalSourcesExtension
import org.jetbrains.kotlin.extensions.CompilerConfigurationExtension
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.jetbrains.kotlin.extensions.PreprocessedVirtualFileFactoryExtension
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.extensions.ExtraImportsProviderExtension
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.synthetic.SyntheticScopeProviderExtension
import org.jetbrains.kotlin.types.KotlinType

@OptIn(ExperimentalCompilerApi::class)
interface InternalRegistry : ConfigSyntax {

  fun intercept(ctx: CompilerContext): List<CliPlugin>

  fun meta(vararg phases: ExtensionPhase): List<ExtensionPhase> = phases.toList()

  private fun CompilerPluginRegistrar.ExtensionStorage.registerPostAnalysisContextEnrichment(
    ctx: CompilerContext
  ) {
    cli {
      AnalysisHandlerExtension.registerExtension(
        object : AnalysisHandlerExtension {
          override fun doAnalysis(
            project: Project,
            module: ModuleDescriptor,
            projectContext: ProjectContext,
            files: Collection<KtFile>,
            bindingTrace: BindingTrace,
            componentProvider: ComponentProvider
          ): AnalysisResult? {
            ctx.module = module
            ctx.componentProvider = componentProvider
            return null
          }

          override fun analysisCompleted(
            project: Project,
            module: ModuleDescriptor,
            bindingTrace: BindingTrace,
            files: Collection<KtFile>
          ): AnalysisResult? {
            ctx.module = module
            return super.analysisCompleted(project, module, bindingTrace, files)
          }
        }
      )
    }
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerProjectComponents(
    project: MockProject,
    configuration: CompilerConfiguration
  ) {
    ide { println("registerProjectComponents!!!! CALLED in IDEA!!!! something is wrong.") }
    registerMetaComponents(configuration)
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerMetaComponents(
    configuration: CompilerConfiguration,
    context: CompilerContext? = null
  ) {
    cli { registerSyntheticScopeProviderIfNeeded() }
    val ctx: CompilerContext =
      if (context != null) {
        context
      } else {
        val messageCollector: MessageCollector? = cli {
          configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        }
        CompilerContext(configuration, messageCollector)
      }
    registerPostAnalysisContextEnrichment(ctx)

    // TODO: Pending fix https://github.com/gradle/gradle/issues/14727
    //    println("System.properties are: " + System.getProperties().map {
    //     "\n${it.key} : ${it.value}"
    //    })
    //
    //    installArrowPlugin()

    val initialPhases = listOf("Initial setup" { listOf(compilerContextService()) })
    (initialPhases + intercept(ctx)).forEach { plugin ->
      println("Registering Cli plugin: $plugin extensions: ${plugin.meta}")
      plugin.meta.invoke(ctx).forEach { currentPhase ->
        fun ExtensionPhase.registerPhase() {
          when (this) {
            // Empty & Composite allow for composing ExtensionPhases
            is ExtensionPhase.Empty -> Unit
            is Composite -> phases.map(ExtensionPhase::registerPhase)
            is CollectAdditionalSources -> registerCollectAdditionalSources(this, ctx)
            is Config -> registerCompilerConfiguration(this, ctx)
            is ExtraImports -> registerExtraImports(this, ctx)
            is PreprocessedVirtualFileFactory -> registerPreprocessedVirtualFileFactory(this, ctx)
            is StorageComponentContainer -> registerStorageComponentContainer(this, ctx)
            is AnalysisHandler -> registerAnalysisHandler(this, ctx)
            is ClassBuilder -> registerClassBuilder(this, ctx)
            is Codegen -> registerCodegen(this, ctx)
            is ClassGeneration -> TODO("ClassGeneration phase is not supported")
            is DeclarationAttributeAlterer -> registerDeclarationAttributeAlterer(this, ctx)
            is PackageProvider -> packageFragmentProvider(this, ctx)
            is SyntheticResolver -> registerSyntheticResolver(this, ctx)
            is IRGeneration -> registerIRGeneration(this, ctx)
            is SyntheticScopeProvider -> registerSyntheticScopeProvider(this, ctx)
            // is DiagnosticsSuppressor -> registerDiagnosticSuppressor(project, this, ctx)
            else ->
              ctx.messageCollector?.report(
                CompilerMessageSeverity.ERROR,
                "Unsupported extension phase: $this"
              )
          }
        }
        currentPhase.registerPhase()
      }
    }
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerSyntheticScopeProviderIfNeeded() {
    /*if (
      !project.extensionArea.hasExtensionPoint(SyntheticScopeProviderExtension.extensionPointName)
    ) {
      SyntheticScopeProviderExtension.registerExtensionPoint(project)
    }*/
  }

  // TODO: Pending fix https://github.com/gradle/gradle/issues/14727
  //  fun installArrowPlugin() {
  //    val ideaPath = System.getProperty("idea.plugins.path")
  //    val userDir = System.getProperty("user.dir")
  //    if (ideaPath != null && ideaPath.isNotEmpty() && userDir != null && userDir.isNotEmpty()) {
  //      println("Installing Arrow Plugin: $ideaPath, $userDir")
  //    }
  //  }

  fun registerMetaAnalyzer(): ExtensionPhase = ExtensionPhase.Empty

  fun CompilerPluginRegistrar.ExtensionStorage.registerExtraImports(
    phase: ExtraImports,
    ctx: CompilerContext
  ) {
    ExtraImportsProviderExtension.registerExtension(
      object : ExtraImportsProviderExtension {
        override fun getExtraImports(ktFile: KtFile): Collection<KtImportInfo> =
          phase.run { ctx.extraImports(ktFile) }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerPreprocessedVirtualFileFactory(
    phase: PreprocessedVirtualFileFactory,
    ctx: CompilerContext
  ) {
    PreprocessedVirtualFileFactoryExtension.registerExtension(
      object : PreprocessedVirtualFileFactoryExtension {
        override fun createPreprocessedFile(file: VirtualFile?): VirtualFile? =
          phase.run { ctx.createPreprocessedFile(file) }

        override fun createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
          phase.run { ctx.createPreprocessedLightFile(file) }

        override fun isPassThrough(): Boolean = phase.run { ctx.isPassThrough() }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerSyntheticScopeProvider(
    phase: SyntheticScopeProvider,
    ctx: CompilerContext
  ) {
    SyntheticScopeProviderExtension.registerExtension(
      object : SyntheticScopeProviderExtension {
        override fun getScopes(
          moduleDescriptor: ModuleDescriptor,
          javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope
        ): List<SyntheticScope> =
          phase.run {
            listOf(
              object : SyntheticScope {
                override fun getSyntheticConstructor(
                  constructor: ConstructorDescriptor
                ): ConstructorDescriptor? = phase.run { ctx.syntheticConstructor(constructor) }

                override fun getSyntheticConstructors(
                  classifierDescriptors: Collection<DeclarationDescriptor>
                ): Collection<FunctionDescriptor> =
                  phase.run { ctx.syntheticConstructors(classifierDescriptors) }

                override fun getSyntheticConstructors(
                  contributedClassifier: ClassifierDescriptor,
                  location: LookupLocation
                ): Collection<FunctionDescriptor> =
                  phase.run { ctx.syntheticConstructors(contributedClassifier, location) }

                override fun getSyntheticExtensionProperties(
                  receiverTypes: Collection<KotlinType>,
                  location: LookupLocation
                ): Collection<PropertyDescriptor> =
                  phase.run { ctx.syntheticExtensionProperties(receiverTypes, location) }

                override fun getSyntheticExtensionProperties(
                  receiverTypes: Collection<KotlinType>,
                  name: Name,
                  location: LookupLocation
                ): Collection<PropertyDescriptor> =
                  phase.run { ctx.syntheticExtensionProperties(receiverTypes, name, location) }

                override fun getSyntheticMemberFunctions(
                  receiverTypes: Collection<KotlinType>
                ): Collection<FunctionDescriptor> =
                  phase.run { ctx.syntheticMemberFunctions(receiverTypes) }

                override fun getSyntheticMemberFunctions(
                  receiverTypes: Collection<KotlinType>,
                  name: Name,
                  location: LookupLocation
                ): Collection<FunctionDescriptor> =
                  phase.run { ctx.syntheticMemberFunctions(receiverTypes, name, location) }

                override fun getSyntheticStaticFunctions(
                  functionDescriptors: Collection<DeclarationDescriptor>
                ): Collection<FunctionDescriptor> =
                  phase.run { ctx.syntheticStaticFunctions(functionDescriptors) }

                override fun getSyntheticStaticFunctions(
                  contributedFunctions: Collection<FunctionDescriptor>,
                  location: LookupLocation
                ): Collection<FunctionDescriptor> =
                  phase.run { ctx.syntheticStaticFunctions(contributedFunctions, location) }
              }
            )
          }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerIRGeneration(
    phase: IRGeneration,
    compilerContext: CompilerContext
  ) {
    IrGenerationExtension.registerExtension(
      object : IrGenerationExtension {
        override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
          phase.run { compilerContext.generate(moduleFragment, pluginContext) }
        }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerSyntheticResolver(
    phase: SyntheticResolver,
    compilerContext: CompilerContext
  ) {
    SyntheticResolveExtension.registerExtension(
      object : SyntheticResolveExtension {
        override fun addSyntheticSupertypes(
          thisDescriptor: ClassDescriptor,
          supertypes: MutableList<KotlinType>
        ) {
          phase.run { compilerContext.addSyntheticSupertypes(thisDescriptor, supertypes) }
        }

        override fun generateSyntheticClasses(
          thisDescriptor: ClassDescriptor,
          name: Name,
          ctx: LazyClassContext,
          declarationProvider: ClassMemberDeclarationProvider,
          result: MutableSet<ClassDescriptor>
        ) {
          phase.run {
            compilerContext.generateSyntheticClasses(
              thisDescriptor,
              name,
              ctx,
              declarationProvider,
              result
            )
          }
        }

        override fun generateSyntheticClasses(
          thisDescriptor: PackageFragmentDescriptor,
          name: Name,
          ctx: LazyClassContext,
          declarationProvider: PackageMemberDeclarationProvider,
          result: MutableSet<ClassDescriptor>
        ) {
          phase.run {
            compilerContext.generatePackageSyntheticClasses(
              thisDescriptor,
              name,
              ctx,
              declarationProvider,
              result
            )
          }
        }

        override fun generateSyntheticMethods(
          thisDescriptor: ClassDescriptor,
          name: Name,
          bindingContext: BindingContext,
          fromSupertypes: List<SimpleFunctionDescriptor>,
          result: MutableCollection<SimpleFunctionDescriptor>
        ) {
          phase.run {
            compilerContext.generateSyntheticMethods(
              thisDescriptor,
              name,
              bindingContext,
              fromSupertypes,
              result
            )
          }
        }

        override fun generateSyntheticProperties(
          thisDescriptor: ClassDescriptor,
          name: Name,
          bindingContext: BindingContext,
          fromSupertypes: ArrayList<PropertyDescriptor>,
          result: MutableSet<PropertyDescriptor>
        ) {
          phase.run {
            compilerContext.generateSyntheticProperties(
              thisDescriptor,
              name,
              bindingContext,
              fromSupertypes,
              result
            )
          }
        }

        override fun getSyntheticCompanionObjectNameIfNeeded(
          thisDescriptor: ClassDescriptor
        ): Name? {
          return phase.run {
            compilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor)
          }
        }

        override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
          return phase.run { compilerContext.getSyntheticFunctionNames(thisDescriptor) }
        }

        override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> {
          return phase.run { compilerContext.getSyntheticNestedClassNames(thisDescriptor) }
        }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.packageFragmentProvider(
    phase: PackageProvider,
    ctx: CompilerContext
  ) {
    PackageFragmentProviderExtension.registerExtension(
      object : PackageFragmentProviderExtension {
        override fun getPackageFragmentProvider(
          project: Project,
          module: ModuleDescriptor,
          storageManager: StorageManager,
          trace: BindingTrace,
          moduleInfo: ModuleInfo?,
          lookupTracker: LookupTracker
        ): PackageFragmentProvider? {
          return phase.run {
            ctx.getPackageFragmentProvider(
              project,
              module,
              storageManager,
              trace,
              moduleInfo,
              lookupTracker
            )
          }
        }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerDeclarationAttributeAlterer(
    phase: DeclarationAttributeAlterer,
    ctx: CompilerContext
  ) {
    DeclarationAttributeAltererExtension.registerExtension(
      object : DeclarationAttributeAltererExtension {
        override fun refineDeclarationModality(
          modifierListOwner: KtModifierListOwner,
          declaration: DeclarationDescriptor?,
          containingDeclaration: DeclarationDescriptor?,
          currentModality: Modality,
          isImplicitModality: Boolean
        ): Modality? {
          return phase.run {
            ctx.refineDeclarationModality(
              modifierListOwner,
              declaration,
              containingDeclaration,
              currentModality,
              isImplicitModality
            )
          }
        }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerCodegen(
    phase: Codegen,
    ctx: CompilerContext
  ) {
    ExpressionCodegenExtension.registerExtension(
      object : ExpressionCodegenExtension {
        override fun applyFunction(
          receiver: StackValue,
          resolvedCall: ResolvedCall<*>,
          c: ExpressionCodegenExtension.Context
        ): StackValue? {
          return phase.run { ctx.applyFunction(receiver, resolvedCall, c) }
        }

        override fun applyProperty(
          receiver: StackValue,
          resolvedCall: ResolvedCall<*>,
          c: ExpressionCodegenExtension.Context
        ): StackValue? {
          return phase.run { ctx.applyProperty(receiver, resolvedCall, c) }
        }

        override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
          phase.run { ctx.generateClassSyntheticParts(codegen) }
        }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerStorageComponentContainer(
    phase: StorageComponentContainer,
    ctx: CompilerContext
  ) {
    StorageComponentContainerContributor.registerExtension(DelegatingContributor(phase, ctx))
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerCollectAdditionalSources(
    phase: CollectAdditionalSources,
    ctx: CompilerContext
  ) {
    cli {
      CollectAdditionalSourcesExtension.registerExtension(
        object : CollectAdditionalSourcesExtension {
          override fun collectAdditionalSourcesAndUpdateConfiguration(
            knownSources: Collection<KtFile>,
            configuration: CompilerConfiguration,
            project: Project
          ): Collection<KtFile> =
            phase.run {
              ctx.collectAdditionalSourcesAndUpdateConfiguration(
                knownSources,
                configuration,
                project
              )
            }
        }
      )
    }
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerAnalysisHandler(
    phase: AnalysisHandler,
    ctx: CompilerContext
  ) {
    phase.pushAnalysisPhase()
    cli {
      AnalysisHandlerExtension.registerExtension(
        object : AnalysisHandlerExtension {
          override fun analysisCompleted(
            project: Project,
            module: ModuleDescriptor,
            bindingTrace: BindingTrace,
            files: Collection<KtFile>
          ): AnalysisResult? =
            phase.run {
              popAnalysisPhase()
              val result = ctx.analysisCompleted(project, module, bindingTrace, files)
              if (result is AnalysisResult.RetryWithAdditionalRoots) willRewind(true)
              when {
                result?.isError() == true -> result
                canRewind() -> {
                  willRewind(false)
                  AnalysisResult.RetryWithAdditionalRoots(
                    bindingTrace.bindingContext,
                    module,
                    emptyList(),
                    emptyList()
                  )
                }
                else -> null
              }
            }

          override fun doAnalysis(
            project: Project,
            module: ModuleDescriptor,
            projectContext: ProjectContext,
            files: Collection<KtFile>,
            bindingTrace: BindingTrace,
            componentProvider: ComponentProvider
          ): AnalysisResult? {
            return phase.run {
              ctx.doAnalysis(
                project,
                module,
                projectContext,
                files,
                bindingTrace,
                componentProvider
              )
            }
          }
        }
      )
    }
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerClassBuilder(
    phase: ClassBuilder,
    ctx: CompilerContext
  ) {
    @Suppress("DEPRECATION_ERROR")
    org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension.registerExtension(
      object :
        @Suppress("DEPRECATION_ERROR")
        org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension {
        override fun interceptClassBuilderFactory(
          interceptedFactory: ClassBuilderFactory,
          bindingContext: BindingContext,
          diagnostics: DiagnosticSink
        ): ClassBuilderFactory =
          phase.run { ctx.interceptClassBuilder(interceptedFactory, bindingContext, diagnostics) }
      }
    )
  }

  fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerConfiguration(
    phase: Config,
    ctx: CompilerContext
  ) {
    CompilerConfigurationExtension.registerExtension(
      object : CompilerConfigurationExtension {
        override fun updateConfiguration(configuration: CompilerConfiguration) {
          phase.run { ctx.updateConfiguration(configuration) }
        }
      }
    )
  }

  class DelegatingContributor(val phase: StorageComponentContainer, val ctx: CompilerContext) :
    StorageComponentContainerContributor {

    override fun registerModuleComponents(
      container: org.jetbrains.kotlin.container.StorageComponentContainer,
      platform: TargetPlatform,
      moduleDescriptor: ModuleDescriptor
    ) {
      phase.run { ctx.registerModuleComponents(container, moduleDescriptor) }
      container.useInstance(
        object : DeclarationChecker {
          override fun check(
            declaration: KtDeclaration,
            descriptor: DeclarationDescriptor,
            context: DeclarationCheckerContext
          ): Unit = phase.run { ctx.check(declaration, descriptor, context) }
        }
      )
    }
  }

  fun compilerContextService(): StorageComponentContainer =
    storageComponent(
      registerModuleComponents = { container, _ -> container.useInstance(this) },
      check = { _, _, _ -> }
    )
}
