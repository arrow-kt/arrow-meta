package arrow.meta.plugin.gradle

import org.gradle.api.Project

class ProofsGradlePlugin : ArrowMetaGradlePlugin() {

  override fun apply(project: Project): Unit {
    super.apply(project)

    addMetaDependency(project, "implementation", "io.arrow-kt:arrow-meta-prelude")
    addCompilerPlugin(project, "arrow-proofs-plugin")
  }
}
