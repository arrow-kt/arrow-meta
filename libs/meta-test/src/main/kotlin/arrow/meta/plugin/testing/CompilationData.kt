package arrow.meta.plugin.testing

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

/**
 * Compilation data is a Monoid that can accumulate in its element as it's composed and merged with
 * other CompilationData elements
 */
@OptIn(ExperimentalCompilerApi::class)
internal data class CompilationData(
  val compilerPlugins: List<String> = emptyList(),
  val metaPlugins: List<CompilerPluginRegistrar> = emptyList(),
  val dependencies: List<String> = emptyList(),
  val sources: List<Code.Source> = emptyList(),
  val arguments: List<String> = emptyList(),
  val commandLineProcessors: List<CommandLineProcessor> = emptyList(),
  val symbolProcessors: List<SymbolProcessorProvider> = emptyList(),
  val pluginOptions: List<PluginOption> = emptyList(),
  val targetVersion: String? = null
) {

  operator fun plus(other: CompilationData): CompilationData =
    copy(
      compilerPlugins = compilerPlugins + other.compilerPlugins,
      metaPlugins = metaPlugins + other.metaPlugins,
      dependencies = dependencies + other.dependencies,
      sources = sources + other.sources,
      arguments = arguments + other.arguments,
      commandLineProcessors = commandLineProcessors + other.commandLineProcessors,
      symbolProcessors = symbolProcessors + other.symbolProcessors,
      pluginOptions = pluginOptions + other.pluginOptions,
      targetVersion = targetVersion ?: other.targetVersion
    )

  companion object {
    val empty: CompilationData = CompilationData()
  }
}
