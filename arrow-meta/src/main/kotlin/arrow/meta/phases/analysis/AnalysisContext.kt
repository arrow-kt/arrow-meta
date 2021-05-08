package arrow.meta.phases.analysis

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

internal typealias PluginAnalysisExtension = Int

/**
 * The Analysis Context represents the environment of all Arrow Meta analysis plugin phases and its behaviors.
 */
internal object AnalysisContext {
  private val willRewind: AtomicBoolean = AtomicBoolean(false)
  private val pluginAnalysisExtensionQueue: Queue<PluginAnalysisExtension> = LinkedList()

  fun AnalysisHandler.pushAnalysisPhase(): Boolean = pluginAnalysisExtensionQueue.offer(0)
  fun AnalysisHandler.popAnalysisPhase(): Unit { if (pluginAnalysisExtensionQueue.isNotEmpty()) pluginAnalysisExtensionQueue.remove() }
  fun AnalysisHandler.willRewind(rewind: Boolean): Unit = willRewind.set(rewind)
  fun AnalysisHandler.canRewind(): Boolean = willRewind.get() && pluginAnalysisExtensionQueue.isEmpty()
}