package arrow.meta.ide

import arrow.meta.ide.phases.resolve.quoteSystemCache
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectLifecycleListener
import com.intellij.util.messages.Topic
import org.jetbrains.kotlin.config.CompilerConfiguration

class MetaRegistrar : ApplicationInitializedListener {
  val LOG = Logger.getInstance("#arrow.AppRegistrar")

  override fun componentsInitialized(): Unit {
    LOG.info("componentsInitialized")
    ApplicationManager.getApplication()?.let { app ->
      LOG.info("subscribing meta registrars")
      val start = System.currentTimeMillis()
      app.register(ProjectLifecycleListener.TOPIC, metaProjectRegistrar, quoteSystemCache) // Alternative use ProjectManagerListener.TOPIC
      LOG.info("subscribing meta registrars took ${System.currentTimeMillis() - start}ms")
    }
    println("componentsInitialized")
  }
}

private fun <A> Application.register(topic: Topic<A>, vararg listeners: A): Unit =
  listeners.toList().forEach { messageBus.connect(this).subscribe(topic, it) }

/**
 * This extension registers a MetaPlugin for a given project.
 */
private val metaProjectRegistrar: ProjectLifecycleListener
  get() = object : ProjectLifecycleListener, Disposable {
    val metaPlugin = IdeMetaPlugin()
    val LOG = Logger.getInstance("#arrow.metaProjectRegistrar")

    override fun projectComponentsInitialized(project: Project) {
      LOG.info("beforeProjectLoaded:${project.name}")
      val start = System.currentTimeMillis()
      val configuration = CompilerConfiguration()
      metaPlugin.registerMetaComponents(project, configuration)
      LOG.info("beforeProjectLoaded:${project.name} took ${System.currentTimeMillis() - start}ms")
    }

    override fun dispose() {
      // TODO: make sure that all registered extensions are disposed
    }
  }
