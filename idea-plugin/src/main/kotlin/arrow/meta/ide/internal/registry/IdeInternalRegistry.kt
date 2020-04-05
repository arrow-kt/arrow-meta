package arrow.meta.ide.internal.registry

import arrow.meta.dsl.platform.ide
import arrow.meta.ide.dsl.application.ServiceKind
import arrow.meta.ide.phases.analysis.MetaIdeAnalyzer
import arrow.meta.ide.phases.application.ApplicationProvider
import arrow.meta.ide.phases.editor.IdeContext
import arrow.meta.ide.phases.editor.action.AnActionExtensionProvider
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.ide.phases.editor.intention.IntentionExtensionProvider
import arrow.meta.ide.phases.editor.syntaxHighlighter.SyntaxHighlighterExtensionProvider
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
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInsight.intention.impl.config.IntentionManagerSettings
import com.intellij.ide.AppLifecycleListener
import com.intellij.lang.LanguageAnnotators
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.impl.ApplicationImpl
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.extensions.DefaultPluginDescriptor
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.serviceContainer.PlatformComponentManagerImpl
import com.intellij.ui.content.ContentFactory
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal interface IdeInternalRegistry : InternalRegistry {

  override fun registerMetaAnalyzer(): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          //println("Registering meta analyzer")
          container.useImpl<MetaIdeAnalyzer>()
          //
        },
        check = { declaration, descriptor, context ->
        }
      )
    } ?: ExtensionPhase.Empty

  override fun CompilerContext.registerIdeExclusivePhase(currentPhase: ExtensionPhase): Unit =
    when (currentPhase) {
      is ExtensionPhase.Empty, is CollectAdditionalSources, is Composite, is Config, is ExtraImports,
      is PreprocessedVirtualFileFactory, is StorageComponentContainer, is AnalysisHandler, is ClassBuilder,
      is Codegen, is DeclarationAttributeAlterer, is PackageProvider, is SyntheticResolver,
      is IRGeneration, is SyntheticScopeProvider -> Unit // filter out ExtensionPhases which happen in the compiler
      is ExtensionProvider<*> -> registerExtensionProvider(currentPhase)
      is AnActionExtensionProvider -> registerAnActionExtensionProvider(currentPhase)
      is IntentionExtensionProvider -> registerIntentionExtensionProvider(currentPhase)
      is SyntaxHighlighterExtensionProvider -> registerSyntaxHighlighterExtensionProvider(currentPhase)
      is ToolwindowProvider -> registerToolwindowProvider(currentPhase)
      is ApplicationProvider -> registerApplicationProvider(currentPhase)
      else -> LOG.error("Unsupported ide extension phase: $currentPhase")
    }

  @Suppress("UNCHECKED_CAST")
  fun registerApplicationProvider(phase: ApplicationProvider): Unit =
    ApplicationManager.getApplication()?.let { app ->
      when (phase) {
        is ApplicationProvider.Service<*> -> phase.run {
          when (kind) {
            ServiceKind.Application -> app.registerService(service as Class<Any>, instance)
            ServiceKind.Project -> app.messageBus.connect(app).subscribe(ProjectLifecycleListener.TOPIC, object : ProjectLifecycleListener {
              override fun projectComponentsInitialized(project: Project): Unit = // true, because Meta Ide is still component based, not service based.
                project.registerService(service as Class<Any>, instance)
            })
          }
        }
        is ApplicationProvider.AppListener -> app.messageBus.connect(app).subscribe(AppLifecycleListener.TOPIC, phase.listener)
        is ApplicationProvider.OverrideService -> phase.run { app.overrideService(from, to, override) }
        is ApplicationProvider.ReplaceService<*> -> phase.run { app.replaceService(service as Class<Any>, instance) }
        is ApplicationProvider.Listener -> app.addApplicationListener(phase.listener, app)
        is ApplicationProvider.UnloadServices -> app.safeAs<PlatformComponentManagerImpl>()?.unloadServices(phase.container)?.forEach { LOG.info("Meta Unloaded Service:$it") }
        ApplicationProvider.StopServicePreloading -> app.safeAs<PlatformComponentManagerImpl>()?.stopServicePreloading()
      }
    }
      ?: LOG.warn("The registration process failed for extension:$phase from arrow.meta.ide.phases.application.ApplicationProvider.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun Application.overrideService(fromService: Class<*>, toService: Class<*>, override: Boolean): Unit =
    (this as? ApplicationImpl)
      ?.registerService(fromService, toService, DefaultPluginDescriptor("overrides service:${fromService.simpleName} to ${toService.simpleName}"), override)
      ?: LOG.error("Service:${fromService.simpleName} could not be OVERRIDDEN properly.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun <T : Any> ComponentManager.registerService(service: Class<T>, instance: T): Unit =
    (this as? PlatformComponentManagerImpl)
      ?.registerServiceInstance(service, instance, DefaultPluginDescriptor("registers service:${service.simpleName}"))
      ?: LOG.error("Service:${service.simpleName} could not be REGISTERED properly.\nPlease raise an Issue in Github: https://github.com/arrow-kt/arrow-meta")

  fun <T : Any> ComponentManager.replaceService(service: Class<T>, instance: T): Unit =
    (this as? PlatformComponentManagerImpl)
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
          window.icon = icon
          window.contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent(content(project, window), "", isLockable))
        }
    }

  fun ToolwindowProvider.UnRegisterToolWindow.unregister(): Unit =
    ToolWindowManager.getInstance(project).unregisterToolWindow(id)

  fun ToolwindowProvider.Notification.register(): Unit =
    ToolWindowManager.getInstance(project).notifyByBalloon(id, type, html, icon, listener)

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

  fun <E> registerExtensionProvider(phase: ExtensionProvider<E>): Unit =
    when (phase) {
      is ExtensionProvider.AddExtension -> phase.run { Extensions.getRootArea().getExtensionPoint(EP_NAME).registerExtension(impl, loadingOrder, IdeContext.dispose) } // fixme: IdeContext needs to be removed
      is ExtensionProvider.AddLanguageExtension -> phase.run { LE.addExplicitExtension(lang, impl) }
      is ExtensionProvider.AddFileTypeExtension -> phase.run { FE.addExplicitExtension(fileType, impl) }
      is ExtensionProvider.AddClassExtension -> phase.run { CE.addExplicitExtension(forClass, impl) }
      is ExtensionProvider.RegisterBaseExtension -> phase.run { Extensions.getRootArea().registerExtensionPoint(EP_NAME.name, aClass.name, kind) }
      is ExtensionProvider.RegisterExtension -> phase.run { Extensions.getRootArea().registerExtensionPoint(EP_NAME.name, aClass.name, kind) }
      is ExtensionProvider.AddLanguageAnnotator -> LanguageAnnotators.INSTANCE.addExplicitExtension(phase.lang, phase.impl)
    }
}
