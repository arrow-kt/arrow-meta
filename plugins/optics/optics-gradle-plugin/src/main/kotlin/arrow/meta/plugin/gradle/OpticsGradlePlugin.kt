package arrow.meta.plugin.gradle

public class OpticsGradlePlugin : ArrowMetaGradlePlugin {
  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-optics-plugin"
  override val version: String = "1.0-SNAPSHOT"
  override val pluginId: String = "optics"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(Triple(groupId, "arrow-optics", "1.0.1"))
}
