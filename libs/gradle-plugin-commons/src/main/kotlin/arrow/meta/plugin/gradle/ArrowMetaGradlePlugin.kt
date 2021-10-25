package arrow.meta.plugin.gradle

import java.util.Properties
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.provider.Provider
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
  }

  override fun getPluginArtifact(): SubpluginArtifact =
    SubpluginArtifact(groupId, artifactId, version)

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
    kotlinCompilation.target.project.provider { emptyList() }

  override fun getCompilerPluginId(): String = pluginId

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
}
