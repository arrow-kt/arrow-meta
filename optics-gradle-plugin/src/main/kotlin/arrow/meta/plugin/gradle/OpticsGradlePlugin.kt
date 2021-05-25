package arrow.meta.plugin.gradle

import org.gradle.api.Project

class OpticsGradlePlugin : ArrowMetaGradlePlugin() {

  override fun apply(project: Project): Unit {
    super.apply(project)

    addMetaDependency(project, "implementation", "io.arrow-kt:arrow-meta-prelude")

    addArrowDependency(project, "implementation", "io.arrow-kt:arrow-core-data")
    addArrowDependency(project, "implementation", "io.arrow-kt:arrow-optics")

    addCompilerPlugin(project, "arrow-optics-plugin")
  }
}
