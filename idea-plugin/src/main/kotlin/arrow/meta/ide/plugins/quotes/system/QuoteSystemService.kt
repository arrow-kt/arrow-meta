package arrow.meta.ide.plugins.quotes.system

import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.quotes.Quote
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import com.intellij.openapi.progress.DumbProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.util.Alarm
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.ui.update.MergingUpdateQueue
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import java.util.ArrayList
import java.util.concurrent.ExecutorService

/**
 * Whereas the [QuoteCache] persists quote transformations,
 * the [QuoteSystemService] is responsible for transforming KtFiles under a given [context].
 */
interface QuoteSystemService {

  /**
   * computational context of [QuoteSystemService].
   */
  interface Ctx {

    /**
     * This pool executes quote system transformations.
     * [ExecutorService] is the computational context if [CacheStrategy.backgroundTask] == true
     * @see computeRefreshCache default implementation
     */
    val cacheExec: ExecutorService

    /**
     * This pool executes non-blocking read actions for document updates.
     */
    val docExec: ExecutorService

    // fixme find a good value for timeout (milliseconds)
    val editorQueue: MergingUpdateQueue
  }

  val context: Ctx

  /**
   * this extension applies the quotes in the Ide for a given [file].
   * Currently hijacking the QuoteSystemService and overriding this function will solely reflect in
   * Ide related changes.
   */
  fun <K : KtElement, P : KtElement, S : Scope<K>> processKtFile(
    file: KtFile,
    on: Class<K>,
    quoteFactory: Quote.Factory<P, K, S>,
    match: K.() -> Boolean,
    map: S.(K) -> Transform<K>
  ): Pair<KtFile, List<Transform<K>>>

  companion object {
    fun defaultCtx(project: Project): Ctx =
      object : Ctx {
        /**
         * This single thread pool avoids concurrent updates to the cache. // <- put this in the implementation
         * This pool has to be shutdown, when the project closes.
         */
        override val cacheExec: ExecutorService = AppExecutorUtil.createBoundedApplicationPoolExecutor("Arrow worker", 1)
        override val docExec: ExecutorService = AppExecutorUtil.createBoundedApplicationPoolExecutor("Arrow doc worker", 1)
        override val editorQueue: MergingUpdateQueue =
          MergingUpdateQueue("arrow doc events", 500, true, null, project, null, Alarm.ThreadToUse.POOLED_THREAD)
      }
  }
}

/**
 * complements the quote service with a cache strategy
 * It aggregates the configs of caching quote transformations
 */
interface CacheStrategy {
  val resetCache: Boolean
  val indicator: ProgressIndicator
}

/**
 * The default strategy resets the quote cache and uses the DumbProgressIndicator
 * @param resetCache defines if all previous transformations should be removed or not. Pass false for incremental updates.
 */
fun cacheStrategy(
  resetCache: Boolean = true,
  indicator: ProgressIndicator = DumbProgressIndicator.INSTANCE
): CacheStrategy =
  object : CacheStrategy {
    override val resetCache: Boolean = resetCache
    override val indicator: ProgressIndicator = indicator
  }
