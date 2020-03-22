package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import io.github.classgraph.ClassGraph
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
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
    val kotlinVersion = project.getKotlinPluginVersion()
    project.extensions.create("arrow", ArrowExtension::class.java)
    project.afterEvaluate { p ->
      p.dependencies.add("kotlinCompilerClasspath", "org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion")
      p.dependencies.add("kotlinCompilerClasspath", "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
      p.dependencies.add("kotlinCompilerClasspath", "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion")

      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += "-Xplugin=${classpathOf("compiler-plugin:$compilerPluginVersion")}"
      }
    }
    project.tasks.register("install-idea-plugin", InstallIdeaPlugin::class.java)

    when {
      inIdea() && pluginsDirExists() && !ideaPluginExists() -> {
        println("Arrow Meta IDEA Plugin is not installed!")
        println("Run 'install-idea-plugin' Gradle task from Intellij IDEA to install it.")
      }
    }
  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}