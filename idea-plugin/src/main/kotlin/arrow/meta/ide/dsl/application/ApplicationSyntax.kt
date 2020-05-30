package arrow.meta.ide.dsl.application

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.annotator.AnnotatorSyntax
import arrow.meta.ide.phases.application.ApplicationProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.components.ServiceDescriptor
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.ModuleListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * multi-purpose algebra to interact with the Ide lifecycle through hijacking and replacing existing services or adding lifecycle related extensions that run at their respective phase.
 * Services can be distributed across the ide in three different kinds at application-level, project-level or module-level.
 * The intellij platform distributes an isolated instance depending on the level - isolated in terms of other instances -,
 * that means an application-level service has solely one instance, whereas module-level services have an instance for each module.
 * Though it is possible to register module-level services with Meta it is not advised to do so, due to high memory consumption.
 */
interface ApplicationSyntax {
  /**
   * registers an application-level service and the instance.
   * The IntelliJ Platform ensures that only one instance of [instance] per application is instantiated.
   * even though the service can be retrieved multiple times with [ServiceManager.getService] or [com.intellij.openapi.project.Project.getService] for project-level services.
   * It is impeccable that there is only one [instance] implementation for a given [service] to fulfill coherence in the ide.
   * The ide will throw an exception, if that premise is not met.
   * ```kotlin:ank:playground
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   *
   * interface MyService {
   *   fun printLn(f: KtNamedFunction): Unit
   * }
   * ```
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.invoke
   * import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   *
   * //sampleStart
   * val IdeMetaPlugin.services: IdePlugin
   *   get() = "Register application-level services" {
   *     meta(
   *       addAppService(MyService::class.java) {
   *         object : MyService {
   *           override fun printLn(f: KtNamedFunction): Unit =
   *             println("Function: ${f.name} returns ${f.resolveToDescriptorIfAny()?.returnType ?: "ERROR"}")
   *         }
   *       }
   *     )
   *   }
   *
   * //sampleEnd
   * interface MyService {
   *   fun printLn(f: KtNamedFunction): Unit
   * }
   * ```
   * The service is now available at runtime and there is also the option to utilise project-level services with [addProjectService] - please check the Docs on how to register those.
   * ```kotlin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.lang.annotation.Annotator
   * import com.intellij.openapi.components.ServiceManager
   * import com.intellij.openapi.project.Project
   * import com.intellij.psi.PsiElement
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   * //sampleStart
   * val IdeMetaPlugin.logAnnotator: IdePlugin
   *   get() = "Log Annotator" {
   *     meta(
   *       addAnnotator( // Annotators traverse PsiElements and are means to write language Plugins
   *         annotator = Annotator { element, holder ->
   *           val project = element.project
   *           project.getService(MyProjectService::class.java)?.hello(element)
   *
   *           element.safeAs<KtNamedFunction>()?.let { f ->
   *             ServiceManager.getService(MyAppService::class.java)?.printLn(f)
   *           }
   *         }
   *       )
   *     )
   *   }
   *
   * //sampleEnd
   * interface MyAppService {
   *   fun printLn(f: KtNamedFunction): Unit
   * }
   *
   * interface MyProjectService {
   *   val project: Project
   *   fun hello(element: PsiElement): Unit
   * }
   * ```
   * @see AnnotatorSyntax
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : Any> IdeMetaPlugin.addAppService(service: Class<A>, instance: (A?) -> A?): ExtensionPhase =
    ApplicationProvider.AppService(service) { service -> instance(service as A?) }

  /**
   * replaces an application [service] with [instance].
   * In this example this [Annotator] needs a different [instance] for `MyService` than what is currently provided - see our example in [addAppService].
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.invoke
   * import arrow.meta.plugins.higherkind.isHigherKindedType
   * import com.intellij.lang.annotation.Annotator
   * import com.intellij.openapi.components.ServiceManager
   * import org.jetbrains.kotlin.psi.KtClass
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * //sampleStart
   * val IdeMetaPlugin.logAnnotator: IdePlugin
   *   get() = "Log Function Annotator" {
   *     meta(
   *       replaceAppService(MyService::class.java) { myOldService ->
   *         object : MyService {
   *           override fun printLn(ktclass: KtClass): Unit =
   *             println("Log ${ktclass.name}, which ${if (isHigherKindedType(ktclass)) "is" else "is not"} a Higher Kinded Type.")
   *         }
   *       },
   *       addAnnotator(
   *         annotator = Annotator { element, holder ->
   *           element.safeAs<KtClass>()?.let { f ->
   *             ServiceManager.getService(MyService::class.java)?.printLn(f)
   *           }
   *         }
   *       )
   *     )
   *   }
   *
   * //sampleEnd
   * interface MyService {
   *   fun printLn(f: KtClass): Unit
   * }
   * ```
   * The same technique applies for every service in the plugin dependencies.
   * @see AnnotatorSyntax
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : Any> IdeMetaPlugin.replaceAppService(service: Class<A>, instance: (A?) -> A): ExtensionPhase =
    ApplicationProvider.ReplaceAppService(service) { service -> instance(service as A?) }

  /**
   * overrides a Service interface [from] with [to], when [override] == true.
   * [from] and [to] need to have a type relationship, either through subtyping or other means like `type-proofs`.
   * The application will raise an error at runtime if the latter is not valid or
   * when [override] == false and there is already a service instance associated with [from].
   */
  fun IdeMetaPlugin.overrideService(from: Class<*>, to: Class<*>, override: Boolean = true): ExtensionPhase =
    ApplicationProvider.OverrideService(from, to, override)

  /**
   * TODO: find a better phase to register this
   * Meanwhile this is deprecated.
   */
  /*@Suppress("UNCHECKED_CAST")
  fun <A : Any> IdeMetaPlugin.replaceProjectService(service: Class<A>, instance: (Project, A?) -> A): ExtensionPhase =
    ApplicationProvider.ReplaceProjectService(service) { project, service -> instance(project, service as A?) }
  */

  /**
   * registers a project service instance once all project components are initialized.
   * Contrary to [addAppService] this extension only works for existing services that have been declared in the `plugin.xml`, due to it's current implementation in the Meta internals.
   * @param instance hijacks the existing instance [A] from the IDE and registers the new instance [A]. The hijacked instance is preserved, when [instance] returns null.
   * There are several use-cases like [org.jetbrains.kotlin.caches.resolve.KotlinCacheService] for project-level services.
   * The following example registers a logger for the KotlinCacheService by hijacking it's standard implementation from the kotlin plugin.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.openapi.project.Project
   * import com.intellij.psi.PsiFile
   * import org.jetbrains.kotlin.analyzer.ModuleInfo
   * import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
   * import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
   * import org.jetbrains.kotlin.platform.TargetPlatform
   * import org.jetbrains.kotlin.psi.KtElement
   * import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache
   * //sampleStart
   * val IdeMetaPlugin.logKotlinCachePlugin: IdePlugin
   *   get() = "Log Kotlin Cache Plugin" {
   *     meta(
   *       addProjectService(KotlinCacheService::class.java) { project: Project, kotlinCache: KotlinCacheService? ->
   *         kotlinCache?.let(::logKotlinCache)
   *       }
   *     )
   *   }
   * //sampleEnd
   * fun logKotlinCache(delegate: KotlinCacheService): KotlinCacheService =
   *   object : KotlinCacheService by delegate {
   *     override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade {
   *       println("Meaningful Log message for $elements")
   *       return delegate.getResolutionFacade(elements)
   *     }
   *
   *     override fun getResolutionFacade(elements: List<KtElement>, platform: TargetPlatform): ResolutionFacade {
   *       println("Meaningful Log message for $elements based on target:$platform")
   *       return delegate.getResolutionFacade(elements, platform)
   *     }
   *
   *     override fun getResolutionFacadeByFile(file: PsiFile, platform: TargetPlatform): ResolutionFacade? {
   *       println("Meaningful Log message for $file based on target:$platform")
   *       return delegate.getResolutionFacadeByFile(file, platform)
   *     }
   *
   *     override fun getResolutionFacadeByModuleInfo(moduleInfo: ModuleInfo, platform: TargetPlatform): ResolutionFacade? {
   *       println("Meaningful Log message for module ${moduleInfo.name} based on target:$platform")
   *       return delegate.getResolutionFacadeByModuleInfo(moduleInfo, platform)
   *     }
   *
   *     override fun getSuppressionCache(): KotlinSuppressCache {
   *       println("Meaningful Log message for KotlinSuppressCache")
   *       return delegate.getSuppressionCache()
   *     }
   *   }
   * ```
   * With this example in mind, the usage of KotlinCacheService implies logging the example output on console.
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : Any> IdeMetaPlugin.addProjectService(service: Class<A>, instance: (Project, A?) -> A?): ExtensionPhase =
    ApplicationProvider.ProjectService(service) { project, service -> instance(project, service as A?) }

  /**
   * accumulates all available services for loaded Plugins
   */
  val ApplicationSyntax.availableServices: List<ServiceDescriptor>
    get() = PluginManagerCore.getLoadedPlugins().mapNotNull { it.safeAs<IdeaPluginDescriptorImpl>()?.project?.services?.filterNotNull() }.flatten()

  /**
   * @see StartupActivity
   * @see StartupActivity.DumbAware and their Subtypes
   */
  fun IdeMetaPlugin.addPostStartupActivity(activity: StartupActivity): ExtensionPhase =
    extensionProvider(StartupActivity.POST_STARTUP_ACTIVITY, activity, LoadingOrder.FIRST)

  /**
   * registers an [StartupActivity.Background], which is isomorphic to [StartupActivity] only with a 5 second delay executing [StartupActivity.runActivity].
   * See https://github.com/JetBrains/intellij-community/blob/master/platform/service-container/overview.md#startup-activity
   * @see StartupActivity.Background and their Subtypes
   */
  fun IdeMetaPlugin.addBackgroundPostStartupActivity(activity: StartupActivity.Background): ExtensionPhase =
    extensionProvider(StartupActivity.BACKGROUND_POST_STARTUP_ACTIVITY, activity, LoadingOrder.FIRST)

  fun IdeMetaPlugin.stopServicePreloading(): ExtensionPhase =
    ApplicationProvider.StopServicePreloading

  fun IdeMetaPlugin.addPreloadingActivity(activity: PreloadingActivity): ExtensionPhase =
    extensionProvider(PreloadingActivity.EP_NAME, activity, LoadingOrder.FIRST)

  /**
   * registers an activity, which is executed eagerly in the background on startup.
   * @see PreloadingActivity
   */
  fun IdeMetaPlugin.addPreloadingActivity(preload: (ProgressIndicator) -> Unit): ExtensionPhase =
    addPreloadingActivity(preloadingActivity(preload))

  fun IdeMetaPlugin.addAppLifecycleListener(listener: AppLifecycleListener): ExtensionPhase =
    ApplicationProvider.AppListener(listener)

  /**
   * registers an [AppLifecycleListener].
   * ```kotlin:ank
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.invoke
   *
   * val IdeMetaPlugin.goodbye: IdePlugin
   *   get() = "Goodbye after Application is closed" {
   *     meta(
   *       addAppLifecycleListener(
   *         appClosing = {
   *           println("Ciao!, Au revoir!, Adeus!, Tot ziens!, Пока!, ¡Adiós!, Tschüss!, 再见")
   *         }
   *       )
   *     )
   *   }
   * ```
   */
  fun IdeMetaPlugin.addAppLifecycleListener(
    appClosing: () -> Unit = Noop.effect0,
    projectOpenFailed: () -> Unit = Noop.effect0,
    appFrameCreated: (cliArgs: MutableList<String>) -> Unit = Noop.effect1,
    welcomeScreenDisplayed: () -> Unit = Noop.effect0,
    projectFrameClosed: () -> Unit = Noop.effect0,
    appStarting: (cliProject: Project?) -> Unit = Noop.effect1,
    appWillBeClosed: (restarted: Boolean) -> Unit = Noop.effect1
  ): ExtensionPhase =
    addAppLifecycleListener(appLifecycleListener(appClosing, projectOpenFailed, appFrameCreated, welcomeScreenDisplayed, projectFrameClosed, appStarting, appWillBeClosed))

  fun ApplicationSyntax.preloadingActivity(preload: (ProgressIndicator) -> Unit): PreloadingActivity =
    object : PreloadingActivity() {
      override fun preload(indicator: ProgressIndicator): Unit = preload(indicator)
    }

  fun ApplicationSyntax.appLifecycleListener(
    appClosing: () -> Unit = Noop.effect0,
    projectOpenFailed: () -> Unit = Noop.effect0,
    appFrameCreated: (cliArgs: MutableList<String>) -> Unit = Noop.effect1,
    welcomeScreenDisplayed: () -> Unit = Noop.effect0,
    projectFrameClosed: () -> Unit = Noop.effect0,
    appStarting: (cliProject: Project?) -> Unit = Noop.effect1,
    appWillBeClosed: (restarted: Boolean) -> Unit = Noop.effect1
  ): AppLifecycleListener =
    object : AppLifecycleListener {
      override fun appClosing(): Unit = appClosing()
      override fun projectOpenFailed(): Unit = projectOpenFailed()
      override fun appFrameCreated(cliArgs: MutableList<String>): Unit = appFrameCreated(cliArgs)
      override fun welcomeScreenDisplayed(): Unit = welcomeScreenDisplayed()
      override fun projectFrameClosed(): Unit = projectFrameClosed()
      override fun appStarting(project: Project?): Unit = appStarting(project)
      override fun appWillBeClosed(restarted: Boolean): Unit = appWillBeClosed(restarted)
    }

  /**
   * registers a [ProjectLifecycle]
   */
  fun IdeMetaPlugin.addProjectLifecycle(
    initialize: ProjectLifecycle.(Project) -> Unit = Noop.effect2,
    afterProjectClosed: ProjectLifecycle.(Project) -> Unit = Noop.effect2,
    dispose: ProjectLifecycle.() -> Unit = Noop.effect1,
    beforeProjectLoaded: ProjectLifecycle.(Project) -> Unit = Noop.effect2
  ): ExtensionPhase =
    ApplicationProvider.ProjectListener(projectLifecycleListener(beforeProjectLoaded, initialize, afterProjectClosed, dispose))

  /**
   * Order: [beforeProjectLoaded] then [initialize] then [afterProjectClosed]
   */
  fun ApplicationSyntax.projectLifecycleListener(
    beforeProjectLoaded: ProjectLifecycle.(Project) -> Unit = Noop.effect2,
    initialize: ProjectLifecycle.(Project) -> Unit = Noop.effect2,
    afterProjectClosed: ProjectLifecycle.(Project) -> Unit = Noop.effect2,
    dispose: ProjectLifecycle.() -> Unit = Noop.effect1
  ): ProjectLifecycle =
    object : ProjectLifecycle {
      override fun projectComponentsInitialized(project: Project): Unit =
        initialize(this, project)

      override fun beforeProjectLoaded(project: Project): Unit =
        beforeProjectLoaded(this, project)

      override fun afterProjectClosed(project: Project): Unit =
        afterProjectClosed(this, project)

      override fun dispose(): Unit = dispose(this)
    }

  /**
   * registers a [ProjectLifecycleListener]
   */
  fun ApplicationSyntax.addProjectLifecycleListener(
    beforeProjectLoaded: (Project) -> Unit = Noop.effect1,
    initialize: (Project) -> Unit = Noop.effect1,
    afterProjectClosed: (Project) -> Unit = Noop.effect1
  ): ExtensionPhase =
    ApplicationProvider.ProjectListener(projectLifecycleListener(beforeProjectLoaded, initialize, afterProjectClosed))

  fun ApplicationSyntax.projectLifecycleListener(
    beforeProjectLoaded: (Project) -> Unit = Noop.effect1,
    initialize: (Project) -> Unit = Noop.effect1,
    afterProjectClosed: (Project) -> Unit = Noop.effect1
  ): ProjectLifecycleListener =
    object : ProjectLifecycleListener {
      override fun projectComponentsInitialized(project: Project): Unit =
        initialize(project)

      override fun beforeProjectLoaded(project: Project): Unit =
        beforeProjectLoaded(project)

      override fun afterProjectClosed(project: Project): Unit =
        afterProjectClosed(project)
    }

  fun IdeMetaPlugin.addPMListener(
    closing: (Project) -> Unit = Noop.effect1,
    closed: (Project) -> Unit = Noop.effect1,
    closingBeforeSave: (Project) -> Unit = Noop.effect1,
    opened: (Project) -> Unit = Noop.effect1
  ): ExtensionPhase =
    ApplicationProvider.PMListener(PMListener(closing, closed, closingBeforeSave, opened))

  fun ApplicationSyntax.PMListener(
    closing: (Project) -> Unit = Noop.effect1,
    closed: (Project) -> Unit = Noop.effect1,
    closingBeforeSave: (Project) -> Unit = Noop.effect1,
    opened: (Project) -> Unit = Noop.effect1
  ): ProjectManagerListener =
    object : ProjectManagerListener {
      override fun projectClosing(project: Project):Unit =
        closing(project)

      override fun projectClosed(project: Project): Unit =
        closed(project)

      override fun projectClosingBeforeSave(project: Project): Unit =
        closingBeforeSave(project)

      override fun projectOpened(project: Project):Unit =
        opened(project)
    }

  /**
   * convenience extension to register the CliPlugins from an [IdeMetaPlugin] Plugin
   */
  fun IdeMetaPlugin.registerMetaPlugin(
    conf: CompilerConfiguration = CompilerConfiguration(),
    dispose: ProjectLifecycle.() -> Unit = Noop.effect1
  ): ExtensionPhase =
    addProjectLifecycle(
      initialize = { project: Project ->
        registerMetaComponents(project, conf, project.ctx())
      },
      dispose = dispose
    )

  /**
   * registers an [ModuleListener]
   */
  fun IdeMetaPlugin.addModuleListener(
    moduleAdded: (project: Project, module: Module) -> Unit,
    moduleRemoved: (project: Project, module: Module) -> Unit,
    beforeModuleRemoved: (project: Project, module: Module) -> Unit,
    modulesRenamed: (project: Project, modules: List<Module>, oldNames: (Module) -> String) -> Unit
  ): ExtensionPhase =
    ApplicationProvider.MetaModuleListener(moduleListener(moduleAdded, moduleRemoved, beforeModuleRemoved, modulesRenamed))

  fun ApplicationSyntax.moduleListener(
    moduleAdded: (project: Project, module: Module) -> Unit,
    moduleRemoved: (project: Project, module: Module) -> Unit,
    beforeModuleRemoved: (project: Project, module: Module) -> Unit,
    modulesRenamed: (project: Project, modules: List<Module>, oldNames: (Module) -> String) -> Unit
  ): ModuleListener =
    object : ModuleListener {
      override fun moduleRemoved(project: Project, module: Module): Unit =
        moduleRemoved(project, module)

      override fun beforeModuleRemoved(project: Project, module: Module): Unit =
        beforeModuleRemoved(project, module)

      override fun modulesRenamed(project: Project, modules: MutableList<Module>, oldNameProvider: Function<Module, String>): Unit =
        modulesRenamed(project, modules.toList()) { oldNameProvider.`fun`(it) }

      override fun moduleAdded(project: Project, module: Module): Unit =
        moduleAdded(project, module)
    }

  fun PsiElement.ctx(): CompilerContext? =
    project.ctx()

  fun Project.ctx(): CompilerContext? =
    getService(CompilerContext::class.java)
}
