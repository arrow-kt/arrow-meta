package arrow.meta.plugin.gradle

public class ProofsGradlePlugin : ArrowMetaGradlePlugin {
  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-proofs-plugin"
  override val version: String = "1.5.31-SNAPSHOT"
  override val pluginId: String = "io.arrow-kt.proofs"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(
      Triple(groupId, "arrow-meta-prelude", version),
    )
}
