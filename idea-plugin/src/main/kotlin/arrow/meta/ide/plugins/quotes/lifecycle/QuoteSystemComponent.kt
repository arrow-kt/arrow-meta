package arrow.meta.ide.plugins.quotes.lifecycle

import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.resolve.HighlightingCache
import arrow.meta.ide.plugins.quotes.resolve.QuoteHighlightingCache
import arrow.meta.ide.plugins.quotes.system.QuoteSystemService
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.BoundedTaskExecutor
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * QuoteSystemComponent is a project component which manages the transformations of KtFiles by the quote system.
 *
 * When initialized, it transforms all .kt files of the project in a background thread.
 */
class QuoteSystemComponent(private val project: Project) : ProjectComponent, Disposable {

  val cache: QuoteCache = project.getService(QuoteCache::class.java) // ill typed, fixme

  val system: QuoteSystemService = project.getService(QuoteSystemService::class.java)!!

  override fun initComponent() {
    initializeQuotes(project, system, cache)
  }

  override fun projectOpened() {
    // add a startup activity to populate the cache with a transformation of all project files
    //quoteProjectOpened(project)
  }

  override fun dispose() {
    try {
      system.context.cacheExec.safeAs<BoundedTaskExecutor>()?.shutdownNow()
    } catch (e: Exception) {
      LOG.warn("error shutting down pool", e)
    }
    cache.clear()
    project.getService(QuoteHighlightingCache::class.java)?.map { // resets the highlighter
      HighlightingCache()
    }
  }
}
