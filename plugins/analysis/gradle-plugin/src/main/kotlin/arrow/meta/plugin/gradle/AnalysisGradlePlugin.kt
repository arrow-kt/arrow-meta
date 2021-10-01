package arrow.meta.plugin.gradle

import org.gradle.api.Project

class AnalysisGradlePlugin : ArrowMetaGradlePlugin() {

  override fun apply(project: Project): Unit {
    super.apply(project)
    addMetaDependency(project, "kotlinCompilerClasspath", "io.arrow-kt:arrow-analysis-types-jvm")
    addMetaDependency(project, "implementation", "io.arrow-kt:arrow-analysis-types-jvm")
    addCompilerPlugin(project, "arrow-analysis-kotlin-plugin")
  }
}
