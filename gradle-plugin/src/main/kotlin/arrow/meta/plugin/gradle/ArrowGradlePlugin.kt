package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.github.classgraph.ClassGraph
import org.gradle.api.InvalidUserDataException
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.Properties

/**
 * The project-level Gradle plugin behavior.
 * revisit [org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension] and [MultiplatformPlugin] from Spek to move forward for Mpp
 */
class ArrowGradlePlugin : Plugin<Project> {

  override fun apply(project: Project): Unit {
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
    val compilerPluginVersion = properties.getProperty("COMPILER_PLUGIN_VERSION")
    val kotlinVersion = properties.getProperty("KOTLIN_VERSION")
    if (kotlinVersion != project.getKotlinPluginVersion())
      throw InvalidUserDataException("Use Kotlin $kotlinVersion for Arrow Meta Gradle Plugin")

    val pluginsList = project.extensions.create("arrowMeta", PluginsListExtension::class.java)
    project.afterEvaluate { p ->
      p.dependencies.add("implementation", "io.arrow-kt:arrow-meta-prelude:$compilerPluginVersion")
      p.dependencies.add("implementation", "io.arrow-kt:arrow-refined-types-jvm:$compilerPluginVersion")

      when {
        pluginsList.plugins.isNotEmpty() -> {
          // To add its transitive dependencies
          p.dependencies.add("kotlinCompilerClasspath", "io.arrow-kt:arrow-meta:$compilerPluginVersion")

          p.tasks.withType(KotlinCompile::class.java).configureEach {
            it.kotlinOptions.freeCompilerArgs += listOf(
              "-Xplugin=${classpathOf("arrow-meta:$compilerPluginVersion")}",
              "-P", "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=${p.buildDir.absolutePath}"
            )
          }
        }
      }

      pluginsList.plugins
        .map { plugin ->
          when {
            plugin.endsWith(".jar") -> plugin
            plugin.contains(':') -> classpathOf(plugin)
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

  private fun classpathOf(dependency: String): String {
    try {
      val regex = Regex(".*${dependency.replace(':', '-')}.*")
      return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }.toString()
    } catch (e: NoSuchElementException) {
      throw InvalidUserDataException("$dependency not found")
    }
  }
}
