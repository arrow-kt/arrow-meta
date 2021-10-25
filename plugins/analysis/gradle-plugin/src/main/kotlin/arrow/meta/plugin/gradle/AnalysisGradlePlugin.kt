package arrow.meta.plugin.gradle

public class AnalysisGradlePlugin : ArrowMetaGradlePlugin {
  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-analysis-kotlin-plugin"
  override val version: String = "1.5.31-SNAPSHOT"
  override val pluginId: String = "io.arrow-kt.analysis"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(
      Triple(groupId, "analysis-laws", version),
      Triple(groupId, "analysis-types", version),
    )
}
