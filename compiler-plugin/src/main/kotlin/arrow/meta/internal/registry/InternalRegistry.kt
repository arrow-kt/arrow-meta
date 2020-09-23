package arrow.meta.internal.registry

import arrow.meta.CliPlugin
import arrow.meta.dsl.config.ConfigSyntax
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.phases.codegen.asm.ClassBuilder
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
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
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
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

interface InternalRegistry : ConfigSyntax {

  fun intercept(ctx: CompilerContext): List<CliPlugin>

  fun meta(vararg phases: ExtensionPhase): List<ExtensionPhase> =
    phases.toList()

  private fun registerPostAnalysisContextEnrichment(project: Project, ctx: CompilerContext) {
    cli {
      AnalysisHandlerExtension.registerExtension(project, object : AnalysisHandlerExtension {
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
      })
    }
  }

  fun registerProjectComponents(
    project: MockProject,
    configuration: CompilerConfiguration
  ) {
    ide {
      println("registerProjectComponents!!!! CALLED in IDEA!!!! something is wrong.")
    }
    registerMetaComponents(project, configuration)
  }

  fun registerMetaComponents(
    project: Project,
    configuration: CompilerConfiguration,
    context: CompilerContext? = null
  ) {
    cli {
      println("it's the CLI plugin")
      registerSyntheticScopeProviderIfNeeded(project)
    }
    ide {
      println("it's the IDEA plugin")
    }
    val ctx: CompilerContext =
      if (context != null) {
        context
      } else {
        val messageCollector: MessageCollector? =
          cli { configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE) }
        CompilerContext(project, messageCollector)
      }
    ctx.configuration = configuration // TODO fix with better strategy to extract current config
    registerPostAnalysisContextEnrichment(project, ctx)

    println("System.properties are: " + System.getProperties().map {
      "\n${it.key} : ${it.value}"
    })

    installArrowPlugin()

    val initialPhases = listOf("Initial setup" {
      listOf(compilerContextService())
    })
    (initialPhases + intercept(ctx)).forEach { plugin ->
      println("Registering plugin: $plugin extensions: ${plugin.meta}")
      plugin.meta.invoke(ctx).forEach { currentPhase ->
        fun ExtensionPhase.registerPhase(): Unit {
          when (this) {
            // Empty & Composite allow for composing ExtensionPhases
            is ExtensionPhase.Empty -> Unit
            is Composite -> phases.map(ExtensionPhase::registerPhase)

            is CollectAdditionalSources -> registerCollectAdditionalSources(project, this, ctx)
            is Config -> registerCompilerConfiguration(project, this, ctx)

            is ExtraImports -> registerExtraImports(project, this, ctx)

            is PreprocessedVirtualFileFactory -> registerPreprocessedVirtualFileFactory(project, this, ctx)
            is StorageComponentContainer -> registerStorageComponentContainer(project, this, ctx)
            is AnalysisHandler -> registerAnalysisHandler(project, this, ctx)
            is ClassBuilder -> registerClassBuilder(project, this, ctx)
            is Codegen -> registerCodegen(project, this, ctx)
            is DeclarationAttributeAlterer -> registerDeclarationAttributeAlterer(project, this, ctx)
            is PackageProvider -> packageFragmentProvider(project, this, ctx)
            is SyntheticResolver -> registerSyntheticResolver(project, this, ctx)
            is IRGeneration -> registerIRGeneration(project, this, ctx)
            is SyntheticScopeProvider -> registerSyntheticScopeProvider(project, this, ctx)
            //is DiagnosticsSuppressor -> registerDiagnosticSuppressor(project, this, ctx)
            else -> ctx.messageCollector?.report(CompilerMessageSeverity.ERROR, "Unsupported extension phase: $this")
          }
        }
        currentPhase.registerPhase()
      }
    }
  }

  fun registerSyntheticScopeProviderIfNeeded(project: Project) {
    if (!project.extensionArea.hasExtensionPoint(SyntheticScopeProviderExtension.extensionPointName)) {
      SyntheticScopeProviderExtension.registerExtensionPoint(project)
    }
  }

  fun installArrowPlugin() {
    val ideaPath = System.getProperty("idea.plugins.path")
    val userDir = System.getProperty("user.dir")
    if (ideaPath != null && ideaPath.isNotEmpty() && userDir != null && userDir.isNotEmpty()) {
      println("Installing Arrow Plugin: $ideaPath, $userDir")
    }
  }

  fun registerMetaAnalyzer(): ExtensionPhase = ExtensionPhase.Empty

  fun registerExtraImports(project: Project, phase: ExtraImports, ctx: CompilerContext) {
    ExtraImportsProviderExtension.registerExtension(project, object : ExtraImportsProviderExtension {
      override fun getExtraImports(ktFile: KtFile): Collection<KtImportInfo> =
        phase.run { ctx.extraImports(ktFile) }

    })
  }

  fun registerPreprocessedVirtualFileFactory(project: Project, phase: PreprocessedVirtualFileFactory, ctx: CompilerContext) {
    PreprocessedVirtualFileFactoryExtension.registerExtension(project, object : PreprocessedVirtualFileFactoryExtension {
      override fun createPreprocessedFile(file: VirtualFile?): VirtualFile? =
        phase.run { ctx.createPreprocessedFile(file) }

      override fun createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
        phase.run { ctx.createPreprocessedLightFile(file) }

      override fun isPassThrough(): Boolean =
        phase.run { ctx.isPassThrough() }
    })
  }

  fun registerSyntheticScopeProvider(project: Project, phase: SyntheticScopeProvider, ctx: CompilerContext) {
    SyntheticScopeProviderExtension.registerExtension(project, object : SyntheticScopeProviderExtension {
      override fun getScopes(moduleDescriptor: ModuleDescriptor, javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope): List<SyntheticScope> =
        phase.run {
          listOf(
            object : SyntheticScope {
              override fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? =
                phase.run { ctx.syntheticConstructor(constructor) }

              override fun getSyntheticConstructors(classifierDescriptors: Collection<DeclarationDescriptor>): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticConstructors(classifierDescriptors) }

              override fun getSyntheticConstructors(contributedClassifier: ClassifierDescriptor, location: LookupLocation): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticConstructors(contributedClassifier, location) }

              override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
                phase.run { ctx.syntheticExtensionProperties(receiverTypes, location) }

              override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
                phase.run { ctx.syntheticExtensionProperties(receiverTypes, name, location) }

              override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticMemberFunctions(receiverTypes) }

              override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticMemberFunctions(receiverTypes, name, location) }

              override fun getSyntheticStaticFunctions(functionDescriptors: Collection<DeclarationDescriptor>): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticStaticFunctions(functionDescriptors) }

              override fun getSyntheticStaticFunctions(contributedFunctions: Collection<FunctionDescriptor>, location: LookupLocation): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticStaticFunctions(contributedFunctions, location) }
            }
          )
        }
    })
  }

  fun registerIRGeneration(
    project: Project,
    phase: IRGeneration,
    compilerContext: CompilerContext
  ) {
    IrGenerationExtension.registerExtension(project, object : IrGenerationExtension {
      override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
      ) {
        phase.run { moduleFragment.files.forEach { compilerContext.generate(it, pluginContext) }}
      }
    })
  }

  fun registerSyntheticResolver(
    project: Project,
    phase: SyntheticResolver,
    compilerContext: CompilerContext
  ) {
    SyntheticResolveExtension.registerExtension(project, object : SyntheticResolveExtension {
      override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
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

      override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? {
        return phase.run { compilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor) }
      }

      override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        return phase.run { compilerContext.getSyntheticFunctionNames(thisDescriptor) }
      }

      override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> {
        return phase.run { compilerContext.getSyntheticNestedClassNames(thisDescriptor) }
      }
    })
  }

  fun packageFragmentProvider(
    project: Project,
    phase: PackageProvider,
    ctx: CompilerContext
  ) {
    PackageFragmentProviderExtension.registerExtension(
      project,
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
      })
  }

  fun registerDeclarationAttributeAlterer(
    project: Project,
    phase: DeclarationAttributeAlterer,
    ctx: CompilerContext
  ) {
    DeclarationAttributeAltererExtension.registerExtension(
      project,
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
      })
  }

  fun registerCodegen(project: Project, phase: Codegen, ctx: CompilerContext) {
    ExpressionCodegenExtension.registerExtension(project, object : ExpressionCodegenExtension {
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
    })
  }

  fun registerStorageComponentContainer(
    project: Project,
    phase: StorageComponentContainer,
    ctx: CompilerContext
  ) {
    StorageComponentContainerContributor.registerExtension(
      project,
      DelegatingContributor(phase, ctx)
    )
  }

  fun registerCollectAdditionalSources(
    project: Project,
    phase: CollectAdditionalSources,
    ctx: CompilerContext
  ) {
    cli {
      CollectAdditionalSourcesExtension.registerExtension(
        project,
        object : CollectAdditionalSourcesExtension {
          override fun collectAdditionalSourcesAndUpdateConfiguration(
            knownSources: Collection<KtFile>,
            configuration: CompilerConfiguration,
            project: Project
          ): Collection<KtFile> = phase.run {
            ctx.collectAdditionalSourcesAndUpdateConfiguration(knownSources, configuration, project)
          }
        }
      )
    }
  }

  fun registerAnalysisHandler(
    project: Project,
    phase: AnalysisHandler,
    ctx: CompilerContext
  ) {
    cli {
      AnalysisHandlerExtension.registerExtension(project, object : AnalysisHandlerExtension {
        override fun analysisCompleted(
          project: Project,
          module: ModuleDescriptor,
          bindingTrace: BindingTrace,
          files: Collection<KtFile>
        ): AnalysisResult? = phase.run { ctx.analysisCompleted(project, module, bindingTrace, files) }

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
      })
    }
  }

  fun registerClassBuilder(
    project: Project,
    phase: ClassBuilder,
    ctx: CompilerContext
  ) {
    ClassBuilderInterceptorExtension.registerExtension(
      project,
      object : ClassBuilderInterceptorExtension {
        override fun interceptClassBuilderFactory(
          interceptedFactory: ClassBuilderFactory,
          bindingContext: BindingContext,
          diagnostics: DiagnosticSink
        ): ClassBuilderFactory =
          phase.run {
            ctx.interceptClassBuilder(interceptedFactory, bindingContext, diagnostics)
          }
      })
  }

  fun registerCompilerConfiguration(
    project: Project,
    phase: Config,
    ctx: CompilerContext
  ) {
    CompilerConfigurationExtension.registerExtension(
      project,
      object : CompilerConfigurationExtension {
        override fun updateConfiguration(configuration: CompilerConfiguration) {
          phase.run { ctx.updateConfiguration(configuration) }
        }
      })
  }

  class DelegatingContributor(val phase: StorageComponentContainer, val ctx: CompilerContext) : StorageComponentContainerContributor {

    override fun registerModuleComponents(container: org.jetbrains.kotlin.container.StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
      phase.run { ctx.registerModuleComponents(container, moduleDescriptor) }
      container.useInstance(object : DeclarationChecker {
        override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext): Unit =
          phase.run { ctx.check(declaration, descriptor, context) }
      })
    }
  }

  fun compilerContextService(): StorageComponentContainer =
    storageComponent(
      registerModuleComponents = { container, moduleDescriptor ->
        container.useInstance(this)
      },
      check = { declaration, descriptor, context ->
      }
    )


}

/**
 * The nastier bits
 */
@Throws(Exception::class)
internal fun setFinalStatic(field: Field, newValue: Any) {
  field.isAccessible = true

  val modifiersField = Field::class.java.getDeclaredField("modifiers")
  modifiersField.isAccessible = true
  modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

  field.set(null, newValue)
}
