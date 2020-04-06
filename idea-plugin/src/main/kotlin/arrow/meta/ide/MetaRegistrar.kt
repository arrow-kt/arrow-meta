package arrow.meta.ide

import arrow.meta.ide.dsl.application.ProjectLifecycle
import arrow.meta.ide.dsl.application.projectLifecycleListener
import arrow.meta.ide.plugins.proofs.lifecycle.proofsLifecycle
import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.lifecycle.initializeQuotes
import arrow.meta.ide.plugins.quotes.lifecycle.quoteConfigs
import arrow.meta.ide.plugins.quotes.resolve.QuoteHighlightingCache
import arrow.meta.ide.plugins.quotes.system.QuoteSystemService
import com.intellij.ide.ApplicationInitializedListener
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
      app.register(ProjectLifecycleListener.TOPIC, metaProjectRegistrar, proofsLifecycle) // Alternative use ProjectManagerListener.TOPIC
      // add quoteLifecycleRegistrar when it is integrated
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

/**
 * [quoteLifecycleRegistrar] addresses ide lifecycle specific manipulates utilizing the [QuoteSystemService], [QuoteCache] and [QuoteHighlightingCache].
 */
val quoteLifecycleRegistrar: ProjectLifecycle
  get() = projectLifecycleListener(
    // the usual registration should be in `beforeProjectOpened`, but this is only possible when #446 is unlocked
    initialize = { project: Project ->
      project.quoteConfigs()?.let { (system, cache) ->
        initializeQuotes(project, system, cache)
      }
    }
    /* TODO: project is already disposed at this point are the following functions needed to preserve the lifecycle
    afterProjectClosed = { project: Project ->
      project.quoteConfigs()?.let { (quoteSystem, cache) ->
        try {
          quoteSystem.context.cacheExec.safeAs<BoundedTaskExecutor>()?.shutdownNow()
        } catch (e: Exception) {
          LOG.warn("error shutting down pool", e)
        } finally {
          cache.clear()
        }
      }
    }*/
  )
