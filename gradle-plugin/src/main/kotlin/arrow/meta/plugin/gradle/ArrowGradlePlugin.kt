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
    val kotlinVersion = properties.getProperty("KOTLIN_VERSION")
    if (kotlinVersion != project.getKotlinPluginVersion())
       throw InvalidUserDataException("Use Kotlin $kotlinVersion for Arrow Meta Gradle Plugin")
    project.extensions.create("arrow", ArrowExtension::class.java)
    project.afterEvaluate { p ->
      // Dependencies that aren't provided by compiler-plugin
      p.dependencies.add("kotlinCompilerClasspath", "org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion")
      p.dependencies.add("kotlinCompilerClasspath", "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
      p.dependencies.add("kotlinCompilerClasspath", "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion")

      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += "-Xplugin=${classpathOf("compiler-plugin:$compilerPluginVersion")}"
      }
    }
    project.tasks.register("install-idea-plugin", InstallIdeaPlugin::class.java) {
      it.group = "Arrow Meta"
      it.description = "Installs the correspondent Arrow Meta IDE Plugin if it's not already installed."
    }
    when {
      inIdea() && pluginsDirExists() && !ideaPluginExists() -> { printMessageForInstallation(compilerPluginVersion) }
    }
  }

  private fun printMessageForInstallation(compilerPluginVersion: String): Unit {
    val versionType = when { compilerPluginVersion.endsWith("SNAPSHOT") -> "snapshot" else -> "release" }
    println("Arrow Meta IDE Plugin is not installed!")
    println("Run 'install-idea-plugin' Gradle task under 'Arrow Meta' group to install it (choose just one project when multi-project)")
    println("Receive update notifications when adding this custom repository: https://meta.arrow-kt.io/idea-plugin/latest-$versionType/updatePlugins.xml")
    println("Guideline: https://www.jetbrains.com/help/idea/managing-plugins.html#repos")
  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}
