package arrow.meta.plugin.testing

import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar

/**
 * Compilation data is a Monoid that can accumulate in its element as it's
 * composed and merged with other CompilationData elements
 */
internal data class CompilationData(
  val compilerPlugins: List<String> = emptyList(),
  val metaPlugins: List<ComponentRegistrar> = emptyList(),
  val dependencies: List<String> = emptyList(),
  val sources: List<Code.Source> = emptyList(),
  val arguments: List<String> = emptyList(),
  val commandLineProcessors: List<CommandLineProcessor> = emptyList(),
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
      pluginOptions = pluginOptions + other.pluginOptions,
      targetVersion = targetVersion ?: other.targetVersion
    )

  companion object {
    val empty: CompilationData = CompilationData()
  }
}
