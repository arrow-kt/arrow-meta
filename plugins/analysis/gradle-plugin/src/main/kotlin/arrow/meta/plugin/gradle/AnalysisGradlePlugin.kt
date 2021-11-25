package arrow.meta.plugin.gradle

public class AnalysisGradlePlugin : ArrowMetaGradlePlugin {
  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-analysis-kotlin-plugin"
  override val version: String = "1.6.0-SNAPSHOT"
  override val pluginId: String = "analysis"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(
      Triple(groupId, "arrow-analysis-laws", version),
      Triple(groupId, "arrow-analysis-types", version),
    )
}
