package arrow.meta.plugins

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.Config
import arrow.meta.plugin.testing.ConfigSyntax
import arrow.meta.plugin.testing.Dependency

fun ConfigSyntax.newMetaDependencies(): List<Config> {
  val currentVersion = System.getProperty("CURRENT_VERSION")
  val proofsCompilerPlugin =
    CompilerPlugin("Arrow Meta Proofs", listOf(Dependency("compiler-plugin-proofs:$currentVersion")))
  return metaDependencies + addCompilerPlugins(proofsCompilerPlugin)
}