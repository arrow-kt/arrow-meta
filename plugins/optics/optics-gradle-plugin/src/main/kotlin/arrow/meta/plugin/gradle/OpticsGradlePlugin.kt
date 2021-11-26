package arrow.meta.plugin.gradle

import java.util.*

public class OpticsGradlePlugin : ArrowMetaGradlePlugin {
  // get the version from the optics.plugin.properties file
  private val properties =
    Properties().also { it.load(this.javaClass.getResourceAsStream("optics.plugin.properties")) }

  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-optics-plugin"
  override val version: String = properties.getProperty("opticsPluginVersion")
  override val pluginId: String = "optics"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(Triple(groupId, "arrow-optics", properties.getProperty("arrowVersion")))
}
