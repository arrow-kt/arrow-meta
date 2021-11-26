package arrow.meta.plugin.gradle

import java.util.*

public class ProofsGradlePlugin : ArrowMetaGradlePlugin {
  // get the version from the optics.plugin.properties file
  private val properties =
    Properties().also { it.load(this.javaClass.getResourceAsStream("proofs.plugin.properties")) }

  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-proofs-plugin"
  override val version: String = properties.getProperty("proofsPluginVersion")
  override val pluginId: String = "proofs"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(
      Triple(groupId, "arrow-meta-prelude", properties.getProperty("metaVersion")),
    )
}
