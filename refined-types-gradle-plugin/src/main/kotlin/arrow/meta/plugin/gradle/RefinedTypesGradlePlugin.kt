package arrow.meta.plugin.gradle

import org.gradle.api.Project

class RefinedTypesGradlePlugin : ArrowMetaGradlePlugin() {

  override fun apply(project: Project): Unit {
    super.apply(project)
    addMetaDependency(project, "kotlinCompilerClasspath", "io.arrow-kt:arrow-refined-types-jvm")
    addMetaDependency(project, "implementation", "io.arrow-kt:arrow-refined-types-jvm")
    addCompilerPlugin(project, "arrow-refined-types-plugin")
  }
}