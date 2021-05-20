package arrow.meta.plugins

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.Config
import arrow.meta.plugin.testing.ConfigSyntax
import arrow.meta.plugin.testing.Dependency

fun ConfigSyntax.newMetaDependencies(): List<Config> {
  val currentVersion = System.getProperty("CURRENT_VERSION")
  val inlineMacrosPlugin =
    CompilerPlugin("InlineMacrosPlugin", listOf(Dependency("arrow-inline-macros-plugin:$currentVersion")))
  return metaDependencies + addCompilerPlugins(inlineMacrosPlugin) + addDependencies(macrosLib(currentVersion))
}