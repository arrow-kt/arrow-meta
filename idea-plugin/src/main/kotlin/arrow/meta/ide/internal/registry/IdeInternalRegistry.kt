package arrow.meta.ide.internal.registry

import arrow.meta.ide.IdePlugin
import arrow.meta.ide.phases.IdeContext
import arrow.meta.ide.phases.application.ApplicationProvider
import arrow.meta.ide.phases.editor.action.AnActionExtensionProvider
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.ide.phases.editor.fileEditor.EditorProvider
import arrow.meta.ide.phases.editor.intention.IntentionExtensionProvider
import arrow.meta.ide.phases.editor.syntaxHighlighter.SyntaxHighlighterExtensionProvider
import arrow.meta.ide.phases.additional.AdditionalIdePhase
import arrow.meta.ide.phases.additional.AdditionalRegistry
import arrow.meta.ide.phases.integration.indices.KotlinIndicesHelper
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.phases.ui.ToolwindowProvider
import arrow.meta.internal.registry.InternalRegistry
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
import com.intellij.ProjectTopics
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInsight.intention.impl.config.IntentionManagerSettings
import com.intellij.ide.AppLifecycleListener
import com.intellij.lang.LanguageAnnotators
import com.intellij.lang.folding.LanguageFolding
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.impl.ApplicationImpl
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.serviceContainer.ComponentManagerImpl
import com.intellij.ui.content.ContentFactory
import com.intellij.util.messages.Topic
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.event.HyperlinkListener


interface IdeInternalRegistry : InternalRegistry {

  fun intercept(ctx: IdeContext): List<IdePlugin>

  fun registerMetaIdeComponents(app: Application) {
    LOG.info("subscribing meta registrars")
    val start = System.currentTimeMillis()
    val ctx = IdeContext(app)
    intercept(ctx).forEach {
      println("Registering ide plugin: $it extensions: ${it.meta}")
      it.meta(ctx).forEach { phase ->
        fun rec(phase: ExtensionPhase): Unit =
          when (phase) {
            is ExtensionPhase.Empty -> Unit
            // cli related extensions
            is CollectAdditionalSources -> ctx.app.registerCliExtension { ctx -> registerCollectAdditionalSources(this, phase, ctx) }
            is Config -> ctx.app.registerCliExtension { ctx -> registerCompilerConfiguration(this, phase, ctx) }
            is ExtraImports -> ctx.app.registerCliExtension { ctx -> registerExtraImports(this, phase, ctx) }
            is PreprocessedVirtualFileFactory -> ctx.app.registerCliExtension { ctx -> registerPreprocessedVirtualFileFactory(this, phase, ctx) }
            is StorageComponentContainer -> ctx.app.registerCliExtension { ctx -> registerStorageComponentContainer(this, phase, ctx) }
            is PackageProvider -> ctx.app.registerCliExtension { ctx -> packageFragmentProvider(this, phase, ctx) }
            is AnalysisHandler -> ctx.app.registerCliExtension { ctx -> registerAnalysisHandler(this, phase, ctx) }
            is ClassBuilder -> ctx.app.registerCliExtension { ctx -> registerClassBuilder(this, phase, ctx) }
            is Codegen -> ctx.app.registerCliExtension { ctx -> registerCodegen(this, phase, ctx) }
            is DeclarationAttributeAlterer -> ctx.app.registerCliExtension { ctx -> registerDeclarationAttributeAlterer(this, phase, ctx) }
            is IRGeneration -> ctx.app.registerCliExtension { ctx -> registerIRGeneration(this, phase, ctx) }
            is SyntheticScopeProvider -> ctx.app.registerCliExtension { ctx -> registerSyntheticScopeProvider(this, phase, ctx) }
            is SyntheticResolver -> ctx.app.registerCliExtension { ctx -> registerSyntheticResolver(this, phase, ctx) }
            // cli-ide integration extensions
            is arrow.meta.ide.phases.integration.SyntheticResolver -> ctx.app.registerCliExtension { ctx ->
              phase.syntheticResolver(this)?.let { phase -> registerSyntheticResolver(this, phase, ctx) }
            }
            is arrow.meta.ide.phases.integration.PackageProvider -> ctx.app.registerCliExtension { ctx ->
              phase.packageFragmentProvider(this)?.let { phase -> packageFragmentProvider(this, phase, ctx) }
            }
            is KotlinIndicesHelper -> ctx.app.registerCliExtension { ctx -> kotlinIndicesHelper(phase, ctx) }
            // TODO: add more integrations
            // ide related extensions
            is ExtensionProvider<*> -> registerExtensionProvider(phase, ctx.app)
            is AnActionExtensionProvider -> registerAnActionExtensionProvider(phase)
            is IntentionExtensionProvider -> registerIntentionExtensionProvider(phase)
            is SyntaxHighlighterExtensionProvider -> registerSyntaxHighlighterExtensionProvider(phase)
            is ToolwindowProvider -> registerToolwindowProvider(phase)
            is EditorProvider -> registerEditorProvider(phase, ctx.app)
            is ApplicationProvider -> registerApplicationProvider(phase, ctx.app)
            is AdditionalIdePhase -> ctx.app.registerAdditional(phase)
            is Composite -> phase.phases.forEach { composite -> rec(composite) }
            else -> LOG.error("Unsupported ide extension phase: $phase")
          }
        rec(phase)
      }
    }
    LOG.info("subscribing meta registrars took ${System.currentTimeMillis() - start}ms")
    println("componentsInitialized")
  }

  fun <A> Application.registerTopic(topic: Topic<A>, listeners: A): Unit =
    messageBus.connect(this).subscribe(topic, listeners)

  fun Application.projectOpened(opened: (Project) -> Unit): Unit =
    registerTopic(ProjectManager.TOPIC, object : ProjectManagerListener {
      override fun projectOpened(project: Project): Unit =
        opened(project)
    })

  fun Application.registerCliExtension(
    f: Project.(CompilerContext) -> Unit
  ): Unit =
    projectOpened { project ->
      project.getService(CompilerContext::class.java)?.let { ctx ->
        f(project, ctx)
      }
    }

  fun Application.registerAdditional(phase: AdditionalIdePhase) {
    getService(AdditionalRegistry::class.java)?.run {
      register(this@registerAdditional, phase)
    }
  }

  fun Project.kotlinIndicesHelper(phase: KotlinIndicesHelper, ctx: CompilerContext): Unit =
    KotlinIndicesHelperExtension.registerExtension(
      this,
      object : KotlinIndicesHelperExtension {
        /**
         * This method is deprecated, even though it is required to be implemented, and won't be called at RunTime
         */
        override fun appendExtensionCallables(consumer: MutableList<in CallableDescriptor>, moduleDescriptor: ModuleDescriptor, receiverTypes: Collection<KotlinType>, nameFilter: (String) -> Boolean): Unit =
          Unit

        override fun appendExtensionCallables(consumer: MutableList<in CallableDescriptor>, moduleDescriptor: ModuleDescriptor, receiverTypes: Collection<KotlinType>, nameFilter: (String) -> Boolean, lookupLocation: LookupLocation): Unit =
          phase.run { ctx.appendExtensionCallables(this@kotlinIndicesHelper, consumer, moduleDescriptor, receiverTypes, nameFilter, lookupLocation) }
      }
    )

  @Suppress("UNCHECKED_CAST")
  fun registerEditorProvider(phase: EditorProvider, app: Application): Unit =
    when (phase) {
      is EditorProvider.FileEditorListener -> app.registerTopic(FileEditorManagerListener.FILE_EDITOR_MANAGER, phase.listener)
    }

  @Suppress("UNCHECKED_CAST")
  fun registerApplicationProvider(phase: ApplicationProvider, app: Application): Unit =
    when (phase) {
      is ApplicationProvider.AppService<*> -> phase.run { instance(app.getService(service))?.let { app.registerService(service as Class<Any>, it) } }
      is ApplicationProvider.ReplaceAppService<*> -> phase.run { app.replaceService(service as Class<Any>, instance(app.getService(service))) }
      is ApplicationProvider.ProjectService<*> -> phase.run {
        /**
         * Investigate other registry options in:
         * com/intellij/serviceContainer/PlatformComponentManagerImpl.kt:163: createComponent
         * com.intellij.openapi.project.impl.ProjectImpl.init
         * com/intellij/idea/ApplicationLoader.kt:261: preloadServices
         */
        app.registerTopic(ProjectManager.TOPIC, object : ProjectManagerListener {
          override fun projectOpened(project: Project): Unit =
            instance(project, project.getService(service))?.let {
              project.registerService(service as Class<Any>, it)
            } ?: Unit
        })
      }
      is ApplicationProvider.ReplaceProjectService<*> -> phase.run {
        app.registerTopic(ProjectManager.TOPIC, object : ProjectManagerListener {
          override fun projectOpened(project: Project): Unit =
            project.replaceService(service as Class<Any>, instance(project, project.getService(service)))
        })
      }
      is ApplicationProvider.AppListener -> app.messageBus.connect(app).subscribe(AppLifecycleListener.TOPIC, phase.listener)
      is ApplicationProvider.OverrideService -> phase.run { app.overrideService(from, to, override) }
      is ApplicationProvider.Listener -> app.addApplicationListener(phase.listener, app)
      is ApplicationProvider.ProjectListener -> app.registerTopic(ProjectLifecycleListener.TOPIC, phase.listener) // Alternative use ProjectManagerListener.TOPIC
      is ApplicationProvider.UnloadServices -> app.safeAs<ComponentManagerImpl>()?.unloadServices(phase.container)?.forEach { LOG.info("Meta Unloaded Service:$it") }
      ApplicationProvider.StopServicePreloading -> app.safeAs<ComponentManagerImpl>()?.stopServicePreloading()
      is ApplicationProvider.MetaModuleListener -> phase.run { app.messageBus.connect(app).subscribe(ProjectTopics.MODULES, listener) }
      is ApplicationProvider.PMListener -> app.registerTopic(ProjectManager.TOPIC, phase.listener)
    }
      ?: LOG.warn("The registration process failed for extension:$phase from arrow.meta.ide.phases.application.ApplicationProvider.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun Application.overrideService(fromService: Class<*>, toService: Class<*>, override: Boolean): Unit =
    (this as? ApplicationImpl)
      ?.registerService(fromService, toService, DefaultPluginDescriptor("overrides service:${fromService.simpleName} to ${toService.simpleName}"), override)
      ?: LOG.error("Service:${fromService.simpleName} could not be OVERRIDDEN properly.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun <T : Any> ComponentManager.registerService(service: Class<T>, instance: T): Unit =
    (this as? ComponentManagerImpl)
      ?.registerServiceInstance(service, instance, DefaultPluginDescriptor("registers service:${service.simpleName}"))
      ?: LOG.error("Service:${service.simpleName} could not be REGISTERED properly.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun <T : Any> ComponentManager.replaceService(service: Class<T>, instance: T): Unit =
    (this as? ComponentManagerImpl)
      ?.replaceServiceInstance(service, instance, this)
      ?: LOG.error("Service:${service.simpleName} could not be REPLACED properly.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun registerToolwindowProvider(phase: ToolwindowProvider): Unit =
    when (phase) {
      is ToolwindowProvider.RegisterToolWindow -> phase.registerOrActivate()
      is ToolwindowProvider.UnRegisterToolWindow -> phase.unregister()
      is ToolwindowProvider.Notification -> phase.register()
    }

  fun ToolwindowProvider.RegisterToolWindow.registerOrActivate(): Unit =
    ToolWindowManager.getInstance(project).let { manager ->
      manager.getToolWindow(id)?.activate(null)
        ?: manager.registerToolWindow(id, canCloseContent, anchor).let { window ->
          window.setIcon(icon)
          window.contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent(content(project, window), "", isLockable))
        }
    }

  fun ToolwindowProvider.UnRegisterToolWindow.unregister(): Unit =
    ToolWindowManager.getInstance(project).unregisterToolWindow(id)

  fun ToolwindowProvider.Notification.register(): Unit =
    ToolWindowManager.getInstance(project).notifyByBalloon(id, type, html, icon, HyperlinkListener(listener))

  fun registerSyntaxHighlighterExtensionProvider(phase: SyntaxHighlighterExtensionProvider): Unit =
    when (phase) {
      is SyntaxHighlighterExtensionProvider.RegisterSyntaxHighlighter -> phase.run {
        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(language, factory)
      }
    }

  fun registerIntentionExtensionProvider(phase: IntentionExtensionProvider): Unit =
    when (phase) {
      is IntentionExtensionProvider.RegisterIntention -> phase.run {
        IntentionManager.getInstance()?.addAction(intention)
          ?: LOG.warn("Couldn't register Intention:${intention.text}")
      }
      is IntentionExtensionProvider.UnregisterIntention -> phase.run {
        IntentionManager.getInstance()?.unregisterIntention(intention)
          ?: LOG.warn("Couldn't unregister Intention:${intention.text}")
      }
      is IntentionExtensionProvider.SetAvailability -> phase.run {
        IntentionManagerSettings.getInstance().setEnabled(intention, enabled)
      }
      is IntentionExtensionProvider.SetAvailabilityOnActionMetaData -> phase.run {
        IntentionManagerSettings.getInstance().setEnabled(intention, enabled)
      }
    }

  fun registerAnActionExtensionProvider(phase: AnActionExtensionProvider): Unit =
    when (phase) {
      is AnActionExtensionProvider.RegisterAction -> phase.run {
        ActionManager.getInstance()?.registerAction(actionId, action)
          ?: LOG.warn("Couldn't register Action:$actionId")
      }
      is AnActionExtensionProvider.UnregisterAction -> phase.run {
        ActionManager.getInstance()?.unregisterAction(actionId)
          ?: LOG.warn("Couldn't unregister Action:$actionId")
      }
      is AnActionExtensionProvider.ReplaceAction -> phase.run {
        ActionManager.getInstance()?.replaceAction(actionId, newAction)
          ?: LOG.warn("Couldn't replace Action:$actionId")
      }
      is AnActionExtensionProvider.AddTimerListener -> phase.run {
        ActionManager.getInstance()?.addTimerListener(delay, listener)
          ?: LOG.warn("Couldn't add TimerListener:$listener")
      }
      is AnActionExtensionProvider.AddTransparentTimerListener -> phase.run {
        ActionManager.getInstance()?.addTransparentTimerListener(delay, listener)
          ?: LOG.warn("Couldn't add TransparentTimerListener:$listener")
      }
      is AnActionExtensionProvider.RemoveTimerListener -> phase.run {
        ActionManager.getInstance()?.removeTimerListener(listener)
          ?: LOG.warn("Couldn't remove TimerListener:$listener")
      }
      is AnActionExtensionProvider.RemoveTransparentTimerListener -> phase.run {
        ActionManager.getInstance()?.removeTransparentTimerListener(listener)
          ?: LOG.warn("Couldn't remove TransparentTimerListener:$listener")
      }
    }

  fun <E> registerExtensionProvider(phase: ExtensionProvider<E>, disposable: Disposable): Unit =
    when (phase) {
      is ExtensionProvider.AddExtension -> phase.run { Extensions.getRootArea().getExtensionPoint(EP_NAME).registerExtension(impl, loadingOrder, disposable) }
      is ExtensionProvider.AddLanguageExtension -> phase.run { LE.addExplicitExtension(lang, impl) }
      is ExtensionProvider.AddFileTypeExtension -> phase.run { FE.addExplicitExtension(fileType, impl) }
      is ExtensionProvider.AddClassExtension -> phase.run { CE.addExplicitExtension(forClass, impl) }
      is ExtensionProvider.RegisterBaseExtension -> phase.run { Extensions.getRootArea().registerExtensionPoint(EP_NAME.name, aClass.name, kind) }
      is ExtensionProvider.RegisterExtension -> phase.run { Extensions.getRootArea().registerExtensionPoint(EP_NAME.name, aClass.name, kind) }
      is ExtensionProvider.AddLanguageAnnotator -> LanguageAnnotators.INSTANCE.addExplicitExtension(phase.lang, phase.impl)
      is ExtensionProvider.AddFoldingBuilder -> LanguageFolding.INSTANCE.addExplicitExtension(phase.lang, phase.impl)
    }
}
