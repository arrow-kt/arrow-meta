package arrow.meta

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

/**
 * CLI bootstrap service
 */
class MetaCliProcessor : CommandLineProcessor {

  /**
   * The Arrow Meta Compiler Plugin Id
   */
  override val pluginId: String = "arrow.meta.plugin.compiler"

  override val pluginOptions: Collection<CliOption> = emptyList()

}
