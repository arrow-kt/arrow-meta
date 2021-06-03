package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.github.classgraph.ClassGraph
import org.gradle.api.InvalidUserDataException
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.Properties

abstract class ArrowMetaGradlePlugin : Plugin<Project> {

  companion object {
    private const val VERSION_KEY = "COMPILER_PLUGIN_VERSION"
  }
  private val properties = Properties()

  override fun apply(project: Project): Unit {
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
    val kotlinVersion = properties.getProperty("KOTLIN_VERSION")
    if (kotlinVersion != project.getKotlinPluginVersion())
      throw InvalidUserDataException("Use Kotlin $kotlinVersion for this Gradle Plugin")

    // To add its transitive dependencies
    addMetaDependency(project, "kotlinCompilerClasspath", "io.arrow-kt:arrow-meta")

    project.afterEvaluate { p ->
      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += listOf(
          "-Xplugin=${classpathOf("arrow-meta")}",
          "-P", "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=${p.buildDir.absolutePath}"
        )
      }
    }
  }

  protected fun addMetaDependency(project: Project, configuration: String, dependency: String) =
    project.afterEvaluate { p ->
      p.dependencies.add(configuration, "$dependency:${properties.getProperty(VERSION_KEY)}")
    }

  protected fun addArrowDependency(project: Project, configuration: String, dependency: String) =
    project.afterEvaluate { p ->
      p.dependencies.add(configuration, "$dependency:${properties.getProperty("ARROW_VERSION")}")
    }

  protected fun addCompilerPlugin(project: Project, plugin: String) =
    project.afterEvaluate { p ->
      println("Applying $plugin for ${project.name} ...")
      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += listOf("-Xplugin=${classpathOf(plugin)}")
      }
    }

  private fun classpathOf(dependency: String): String {
    try {
      val compilerPluginVersion = properties.getProperty(VERSION_KEY)
      val regex = Regex(".*${dependency}-${compilerPluginVersion}.*")
      return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }.toString()
    } catch (e: NoSuchElementException) {
      throw InvalidUserDataException("$dependency not found")
    }
  }
}
