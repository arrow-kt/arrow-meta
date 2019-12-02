package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import io.github.classgraph.ClassGraph
import java.util.Properties

/**
 * Gradle Plugin to enable Arrow Meta.
 *
 * It's published in [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.arrow-kt.arrow).
 *
 * It can be used with the plugins DSL for release versions:
 *
 * ```
 * plugins {
 *   id "io.arrow-kt.arrow" version "<release-version>"
 * }
 * ```
 *
 * In case of using a snapshot version, it must be included with the legacy plugin application:
 *
 * ```
 * buildscript {
 *   repositories {
 *     maven { url "https://plugins.gradle.org/m2/" }
 *     maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local/" }
 *   }
 *   dependencies {
 *     classpath "io.arrow-kt:gradle-plugin:<snapshot-version>"
 *   }
 * }
 *
 * apply plugin: "io.arrow-kt.arrow"
 * ```
 *
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
    project.buildscript.repositories.maven { m ->
      m.setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    }
    project.buildscript.repositories.mavenCentral()

    project.extensions.create("arrow", ArrowExtension::class.java)
    project.afterEvaluate { p ->
      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += "-Xplugin=${classpathOf("arrow-meta-compiler-plugin:$compilerPluginVersion")}"
      }
    }
  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}
