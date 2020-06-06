package arrow.meta.ide.plugins.quotes.highlighting

import arrow.meta.ide.dsl.application.services.Id
import arrow.meta.ide.dsl.application.services.IdService
import arrow.meta.ide.plugins.quotes.lifecycle.QuoteConfigs
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * default are the initial values when project opens. They have to be reset, when it is closed.
 * Please note, that this cache is immutable, but it has to be used in a thread-safe way to make "waitToInitialize" work.
 * Therefore, you have to make values of type "HighlightingCache" volatile.
 */
internal data class HighlightingCache(val initialized: Boolean = false, val latch: CountDownLatch = CountDownLatch(1))

/**
 * TODO: transfer this to a lifecycle extension
 */
internal class QuoteHighlightingCache private constructor() : IdService<HighlightingCache> {
  @Volatile
  override var value: Id<HighlightingCache> =
    Id.just(HighlightingCache())

  /**
   * It is to be noted that [QuoteHighlightingCache] is depending on the [arrow.meta.ide.plugins.quotes.cache.QuoteCache] and [arrow.meta.ide.plugins.quotes.system.QuoteSystem].
   * If those don't exist the UI freezes and Threads shoke.
   * waits until the initial transformation, which is started after the project was initialized,
   * is finished. This is necessary to implement fully working highlighting of .kt files, which
   * access data from the Quote transformations during resolving.
   */
  fun QuoteConfigs.waitToInitialize(): Unit =
    value.extract().let { cache ->
      if (!cache.initialized) { // this is not executed anymore
        cache.latch.await(5, TimeUnit.SECONDS)
        //println("BOOOOOOMMMMMMM")
      } else {
        //println("NALALALLAL")
      }
    }
}