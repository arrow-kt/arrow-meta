package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import io.github.classgraph.ClassGraph
import org.gradle.api.InvalidUserDataException
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.Properties

/**
 * The project-level Gradle plugin behavior.
 * revisit [org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension] and [MultiplatformPlugin] from Spek to move forward for Mpp
 */
class ArrowGradlePlugin : Plugin<Project> {

  companion object {
    fun isEnabled(project: Project): Boolean = project.plugins.findPlugin(ArrowGradlePlugin::class.java) != null
  }

  override fun apply(project: Project): Unit {
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
    val compilerPluginVersion = properties.getProperty("COMPILER_PLUGIN_VERSION")
    val kotlinVersion = properties.getProperty("KOTLIN_VERSION")
    if (kotlinVersion != project.getKotlinPluginVersion())
      throw InvalidUserDataException("Use Kotlin $kotlinVersion for Arrow Meta Gradle Plugin")

    val pluginsList = project.extensions.create("arrowMeta", PluginsListExtension::class.java)
    project.afterEvaluate { p ->
      // To add its transitive dependencies
      p.dependencies.add("kotlinCompilerClasspath", "io.arrow-kt:arrow-meta:$compilerPluginVersion")

      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += listOf(
          "-Xplugin=${classpathOf("arrow-meta:$compilerPluginVersion")}"
          , "-P"
          , "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=${p.buildDir.absolutePath}"
        )
      }
      pluginsList.plugins
        .map { plugin ->
          when {
            plugin.endsWith(".jar") -> plugin
            else -> classpathOf("arrow-$plugin-plugin:$compilerPluginVersion")
          }
        }
        .forEach { plugin ->
          p.tasks.withType(KotlinCompile::class.java).configureEach {
            it.kotlinOptions.freeCompilerArgs += listOf("-Xplugin=$plugin")
          }
        }
    }
  }

  private fun classpathOf(dependency: String): File {
    try {
      val regex = Regex(".*${dependency.replace(':', '-')}.*")
      return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
    } catch (e: NoSuchElementException) {
      throw InvalidUserDataException("$dependency not found")
    }
  }
}
