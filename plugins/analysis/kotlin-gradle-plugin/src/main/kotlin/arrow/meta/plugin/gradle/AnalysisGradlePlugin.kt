package arrow.meta.plugin.gradle

import java.util.*

public class AnalysisGradlePlugin : ArrowMetaGradlePlugin {
  // get the version from the analysis.plugin.properties file
  private val properties =
    Properties().also { it.load(this.javaClass.getResourceAsStream("analysis.plugin.properties")) }

  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-analysis-kotlin-plugin"
  override val version: String = properties.getProperty("analysisPluginVersion")
  override val pluginId: String = "analysis"

  override val dependencies: List<Triple<String, String, String>> =
    listOf(
      Triple(groupId, "arrow-analysis-laws", version),
      Triple(groupId, "arrow-analysis-types", version),
    )
}
