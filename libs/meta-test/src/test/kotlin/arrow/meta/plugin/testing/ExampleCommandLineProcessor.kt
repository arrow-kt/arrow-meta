package arrow.meta.plugin.testing

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

class ExampleCommandLineProcessor : CommandLineProcessor {

  companion object {
    val CLI_OPTION =
      CliOption(
        "key",
        "<key>",
        description = "example",
        required = false,
        allowMultipleOccurrences = false
      )
  }

  override val pluginId: String = "example.of.plugin.id"
  override val pluginOptions: Collection<CliOption> = listOf(CLI_OPTION)
}
