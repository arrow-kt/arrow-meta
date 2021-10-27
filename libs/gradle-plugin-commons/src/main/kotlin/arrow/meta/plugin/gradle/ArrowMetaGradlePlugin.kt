package arrow.meta.plugin.gradle

import io.github.classgraph.ClassGraph
import java.io.File
import java.util.Properties
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

public interface ArrowMetaGradlePlugin : KotlinCompilerPluginSupportPlugin {

  public val groupId: String

  public val artifactId: String

  public val version: String

  public val pluginId: String

  public val dependencies: List<Triple<String, String, String>>

  override fun apply(project: Project): Unit {
    val defaultPath = File("${project.buildDir}/generated/meta/").path

    val extension = project.extensions.create("arrowMeta", ArrowMetaExtension::class.java)
    extension.generatedSrcOutputDir.convention(defaultPath)

    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
    val kotlinVersion = properties.getProperty("KOTLIN_VERSION")
    if (kotlinVersion != project.getKotlinPluginVersion()) {
      throw InvalidUserDataException("Use Kotlin $kotlinVersion for this Gradle Plugin")
    }
    project.afterEvaluate { p ->
      dependencies.forEach { (g, a, v) ->
        val configuration = if (p.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
          "commonMainImplementation"
        } else {
          "implementation"
        }

        p.dependencies.add(configuration, "$g:$a:$v")
      }
    }

    project.afterEvaluate { p ->
      p.extensions.findByType(KotlinProjectExtension::class.java)?.sourceSets?.all { sourceSet ->
        sourceSet.kotlin.srcDirs("${extension.generatedSrcOutputDir.get()}/${sourceSet.name}/")
      }
    }
  }

  override fun getPluginArtifact(): SubpluginArtifact =
    SubpluginArtifact(groupId, artifactId, version)

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    val project = kotlinCompilation.target.project
    val extension = project.extensions.getByType(ArrowMetaExtension::class.java)
    return project.provider {
      listOf(
        SubpluginOption(
          key = "generatedSrcOutputDir",
          value = "${extension.generatedSrcOutputDir.get()}/${kotlinCompilation.defaultSourceSetName}/kotlin"
        ),
      )
    }
  }

  override fun getCompilerPluginId(): String = "arrow.meta.plugin.compiler"

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

  public fun addMetaDependency(
    project: Project,
    configuration: String,
    groupId: String,
    artifactId: String,
    version: String,
  ): Unit =
    project.afterEvaluate { p ->
      p.dependencies.add(configuration, "$groupId:$artifactId:$version")
    }

  private fun classpathOf(dependency: String, properties: Properties): String {
    try {
      val compilerPluginVersion = properties.getProperty(VERSION_KEY)
      val regex = Regex(".*$dependency-$compilerPluginVersion.*")
      return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }.toString()
    } catch (e: NoSuchElementException) {
      throw InvalidUserDataException("$dependency not found")
    }
  }

  private companion object {
    private const val VERSION_KEY = "COMPILER_PLUGIN_VERSION"
  }
}
