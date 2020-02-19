package arrow.meta.ide

import arrow.meta.ide.dsl.application.projectLifecycleListener
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.openapi.startup.StartupManager
import com.intellij.util.messages.Topic
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.debugger.readAction
import org.jetbrains.kotlin.psi.KtFile

class MetaRegistrar : ApplicationInitializedListener {
  val LOG = Logger.getInstance("#arrow.AppRegistrar")

  override fun componentsInitialized(): Unit {
    LOG.info("componentsInitialized")
    ApplicationManager.getApplication()?.let { app ->
      LOG.info("subscribing meta registrars")
      val start = System.currentTimeMillis()
      app.register(ProjectLifecycleListener.TOPIC, metaProjectRegistrar) // Alternative use ProjectManagerListener.TOPIC
      LOG.info("subscribing meta registrars took ${System.currentTimeMillis() - start}ms")
    }
    println("componentsInitialized")
  }
}

private fun <A> Application.register(topic: Topic<A>, vararg listeners: A): Unit =
  listeners.toList().forEach { messageBus.connect(this).subscribe(topic, it) }

private val metaPlugin = IdeMetaPlugin()
/**
 * This extension registers a MetaPlugin for a given project.
 */
private val metaProjectRegistrar: ProjectLifecycleListener
  get() = projectLifecycleListener(
    initialize = { project ->
      val LOG = Logger.getInstance("#arrow.metaProjectRegistrarForProject:${project.name}")
      LOG.info("beforeProjectLoaded:${project.name}")
      val start = System.currentTimeMillis()
      val configuration = CompilerConfiguration()
      metaPlugin.registerMetaComponents(project, configuration)
      LOG.info("beforeProjectLoaded:${project.name} took ${System.currentTimeMillis() - start}ms")
    },
    dispose = {
      // TODO: make sure that all registered extensions are disposed
    }
  )
