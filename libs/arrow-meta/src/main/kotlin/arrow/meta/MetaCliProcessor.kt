package arrow.meta

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

/** CLI bootstrap service */
object ArrowMetaConfigurationKeys {
  val GENERATED_SRC_OUTPUT_DIR: CompilerConfigurationKey<List<String>> =
    CompilerConfigurationKey<List<String>>("directory to locate sources")
}

class MetaCliProcessor : CommandLineProcessor {

  companion object {
    val ARROW_META_GENERATED_SRC_OUTPUT_DIR =
      CliOption(
        "generatedSrcOutputDir",
        "arrow-meta-gen-src-output-dir",
        "Directory to locate generated sources",
        required = false,
        allowMultipleOccurrences = false
      )
  }

  /** The Arrow Meta Compiler Plugin Id */
  override val pluginId: String = "arrow.meta.plugin.compiler"

  override val pluginOptions: Collection<CliOption> = listOf(ARROW_META_GENERATED_SRC_OUTPUT_DIR)

  override fun processOption(
    option: AbstractCliOption,
    value: String,
    configuration: CompilerConfiguration
  ) =
    when (option.optionName) {
      "generatedSrcOutputDir" ->
        configuration.add(ArrowMetaConfigurationKeys.GENERATED_SRC_OUTPUT_DIR, value)
      else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}
