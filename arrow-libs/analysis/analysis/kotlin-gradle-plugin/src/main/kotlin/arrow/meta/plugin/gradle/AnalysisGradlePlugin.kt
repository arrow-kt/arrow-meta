package arrow.meta.plugin.gradle

import java.util.*
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

public class AnalysisGradlePlugin : ArrowMetaGradlePlugin {
  // get the version from the analysis.plugin.properties file
  private val properties =
    Properties().also { it.load(this.javaClass.getResourceAsStream("analysis.plugin.properties")) }

  override val groupId: String = "io.arrow-kt"
  override val artifactId: String = "arrow-analysis-kotlin-plugin"
  override val version: String = properties.getProperty("analysisPluginVersion")
  override val pluginId: String = "analysis"

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
    // do not run on test targets
    if (kotlinCompilation.compilationName.endsWith(
        KotlinCompilation.TEST_COMPILATION_NAME,
        ignoreCase = true
      )
    )
      false
    else super.isApplicable(kotlinCompilation)

  override val dependencies: List<Triple<String, String, String>> =
    listOf(
      Triple(groupId, "arrow-analysis-laws", version),
      Triple(groupId, "arrow-analysis-types", version),
    )
}
