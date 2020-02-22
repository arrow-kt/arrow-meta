package arrow.meta.ide.dsl.application

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.application.ApplicationProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.components.ServiceDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.extensions.LoadingOrder
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

interface ApplicationSyntax {

  /**
   * registers an service and the instance based on its [kind].
   * The IntelliJ Platform ensures that only one instance of [instance] is loaded, when [kind] is [ServiceKind.Application],
   * even though the service can be retrieved multiple times with [ServiceManager.getService] or [com.intellij.openapi.project.Project.getService].
   * It is impeccable that there is only one [instance] implementation for a given [service] to fulfill coherence in the ide.
   * Services have several use-cases like [org.jetbrains.kotlin.caches.resolve.KotlinCacheService] being a project-level service - here [kind] = [ServiceKind.Project], TODO or TODO.
   * Services can be utilised from every in the ide.
   * ```kotlin:ank:playground
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import com.intellij.openapi.project.Project
   *
   * interface MyService { // one application-level service is sufficient
   *   fun printLn(f: KtNamedFunction): Unit
   * }
   *
   * interface MyProjectService { // intended to be used for each Project separately
   *   fun hello(project: Project): Unit
   * }
   * ```
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.dsl.application.ServiceKind
   * import arrow.meta.invoke
   * import com.intellij.openapi.project.Project
   * import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   *
   * //sampleStart
   * val IdeMetaPlugin.services: Plugin
   *   get() = "Register project- and application-level services" {
   *     meta(
   *       addService(
   *         service = MyService::class.java,
   *         kind = ServiceKind.Application,
   *         instance = object : MyService {
   *           override fun printLn(f: KtNamedFunction): Unit =
   *             println("Function: ${f.name} returns ${f.resolveToDescriptorIfAny()?.returnType ?: "ERROR"}")
   *         }
   *       ),
   *       addService(
   *         service = MyProjectService::class.java,
   *         kind = ServiceKind.Project,
   *         instance = object : MyProjectService {
   *           override fun hello(project: Project): Unit =
   *             println("Hello ${project.name}!")
   *         }
   *       )
   *     )
   *   }
   * //sampleEnd
   * interface MyService {
   *   fun printLn(f: KtNamedFunction): Unit
   * }
   * interface MyProjectService {
   *   fun hello(project: Project): Unit
   * }
   * ```
   * Now the instances are available at runtime.
   * ```kotlin:ank
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import com.intellij.lang.annotation.Annotator
   * import com.intellij.openapi.components.ServiceManager
   * import com.intellij.openapi.project.Project
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * val IdeMetaPlugin.logAnnotator: Plugin
   *   get() = "Log Annotator" {
   *     meta(
   *       addAnnotator( // Annotators traverse PsiElements and are means to write language Plugins
   *         annotator = Annotator { element, holder ->
   *           val project = element.project
   *           project.getService(MyProjectService::class.java)?.hello(project)
   *
   *           element.safeAs<KtNamedFunction>()?.let { f ->
   *             ServiceManager.getService(MyService::class.java)?.printLn(f)
   *           }
   *         }
   *       )
   *     )
   *   }
   * ```
   * @see arrow.meta.ide.dsl.editor.annotator.AnnotatorSyntax
   */
  fun <A : Any> IdeMetaPlugin.addService(service: Class<A>, kind: ServiceKind, instance: A): ExtensionPhase =
    ApplicationProvider.Service(service, instance, kind)

  /**
   * replaces a [service] with [instance].
   * The following plugin needs a different [instance] than what 3rd party plugins provide - from our example in [addService].
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.plugins.purity.isImpure
   * import arrow.meta.invoke
   * import com.intellij.lang.annotation.Annotator
   * import com.intellij.openapi.components.ServiceManager
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * //sampleStart
   * val IdeMetaPlugin.logAnnotator: Plugin
   *   get() = "Log Function Annotator" {
   *     meta(
   *       replaceService(
   *         MyService::class.java,
   *         object : MyService {
   *           override fun printLn(f: KtNamedFunction): Unit =
   *             println("Log ${f.name}, which is Impure ${f.isImpure}")
   *         }
   *       ),
   *       addAnnotator(
   *         annotator = Annotator { element, holder ->
   *           element.safeAs<KtNamedFunction>()?.let { f ->
   *             ServiceManager.getService(MyService::class.java)?.printLn(f)
   *           }
   *         }
   *       )
   *     )
   *   }
   * //sampleEnd
   * interface MyService {
   *   fun printLn(f: KtNamedFunction): Unit
   * }
   * ```
   */
  fun <A : Any> IdeMetaPlugin.replaceService(service: Class<A>, instance: A): ExtensionPhase =
    ApplicationProvider.ReplaceService(service, instance)

  /**
   * overrides a Service interface [from] with [to], when [override] == true.
   * [from] and [to] need to have a type relationship, either through subtyping or other means like `type-proofs`.
   * The application will raise an error at runtime if the latter is not valid or
   * when [override] == false and there is already a service instance associated with [from].
   */
  fun IdeMetaPlugin.overrideService(from: Class<*>, to: Class<*>, override: Boolean = true): ExtensionPhase =
    ApplicationProvider.OverrideService(from, to, override)

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
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   *
   * val IdeMetaPlugin.goodbye: Plugin
   *   get() = "Goodbye after Application is closed" {
   *     meta(
   *       addAppLifecycleListener(
   *         appClosing = {
   *           println("Ciao!, Au revoir!, Adeus!, Tot ziens!, Пока!, ¡Adiós!, Tschüss!")
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
}

/**
 * Services can be distributed across the ide in three different kinds at application-level, project-level or module-level.
 * The platform provides an isolated instance depending on the level - isolated in terms of other instances -, that means an application-level service has solely one instance,
 * whereas module-level services have an instance for each module.
 * Though it is possible to construct module-level services with Meta it is not advised to do so, due to high memory consumption.
 */
enum class ServiceKind {
  Application, Project
}