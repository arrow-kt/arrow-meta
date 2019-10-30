package arrow.meta.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 * The compilation-level Gradle plugin for applying the compiler plugin to the Kotlin compiler configuration.
 */
class ArrowKotlinGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
  companion object {
    private const val META_ARTIFACT_NAME = "compiler-plugin"
    private const val GROUP_ID = "io.arrow-kt"
    private const val VERSION = "0.10.3-SNAPSHOT"
    private const val COMPILER_PLUGIN_ID = "arrow.meta.plugin.compiler"
  }

  override fun isApplicable(project: Project, task: AbstractCompile) = ArrowGradlePlugin.isEnabled(project)

  override fun apply(
    project: Project,
    kotlinCompile: AbstractCompile,
    javaCompile: AbstractCompile?,
    variantData: Any?,
    androidProjectHandler: Any?,
    kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
  ): List<SubpluginOption> {

    @Suppress("UNUSED_VARIABLE")
    val extension = project.extensions.findByType(ArrowExtension::class.java)
        ?: ArrowExtension()

    return listOf()
  }

  override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(GROUP_ID, META_ARTIFACT_NAME, VERSION)

  override fun getCompilerPluginId() = COMPILER_PLUGIN_ID
}


