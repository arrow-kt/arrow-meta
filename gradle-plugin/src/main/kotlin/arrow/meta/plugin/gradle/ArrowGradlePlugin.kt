package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import io.github.classgraph.ClassGraph
import java.util.Properties

/**
 * The project-level Gradle plugin behavior that is used specifying the plugin's configuration through the
 * [ArrowExtension] class.
 * revisit [org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension] and [MultiplatformPlugin] from Spek to move forward for Mpp
 */
class ArrowGradlePlugin : Plugin<Project> {

  companion object {
    fun isEnabled(project: Project): Boolean = project.plugins.findPlugin(ArrowGradlePlugin::class.java) != null

    fun getArrowExtension(project: Project): ArrowExtension {
      return project.extensions.getByType(ArrowExtension::class.java)
    }
  }

  override fun apply(project: Project): Unit {
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
    val compilerPluginVersion = properties.getProperty("COMPILER_PLUGIN_VERSION")
    project.extensions.create("arrow", ArrowExtension::class.java)
    project.afterEvaluate { p ->
      p.dependencies.add("kotlinCompilerClasspath", "io.arrow-kt:compiler-plugin:$compilerPluginVersion")

      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += "-Xplugin=${classpathOf("compiler-plugin:$compilerPluginVersion")}"
      }
    }
    val installIdeaPluginTask = project.tasks.register("install-idea-plugin", InstallIdeaPlugin::class.java)
    installIdeaPluginTask.configure { task ->
      task.group = "Arrow Meta"
      task.description = "Installs the correspondent Arrow Meta IDE Plugin if it's not already installed."
    }

    when {
      inIdea() && pluginsDirExists() && !ideaPluginExists() -> {
        println("Arrow Meta IDE Plugin is not installed!")
        println("Run 'install-idea-plugin' Gradle task under 'Arrow Meta' group to install it.")
      }
    }
  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}
