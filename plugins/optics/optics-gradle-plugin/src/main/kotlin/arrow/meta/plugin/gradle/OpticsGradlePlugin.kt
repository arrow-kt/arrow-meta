package arrow.meta.plugin.gradle

import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.util.*

public class OpticsGradlePlugin : Plugin<Project> {
  private val groupId: String = "io.arrow-kt"

  override fun apply(project: Project) {
    // get the version from the optics.plugin.properties file
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("optics.plugin.properties"))
    val version = properties.getProperty("opticsPluginVersion")
    val arrowVersion = properties.getProperty("arrowVersion")

    project.afterEvaluate { p ->
      p.plugins.apply("java-library")
      // add libraries
      p.dependencies.add("api", "$groupId:arrow-annotations:$arrowVersion")
      p.dependencies.add("api", "$groupId:arrow-optics:$arrowVersion")
      // add the plug-in which uses the result of the processor
      p.plugins.apply(KspGradleSubplugin::class.java)
      p.dependencies.add("ksp", "$groupId:arrow-optics-ksp:$version")
      // add the generated files to sourceSets as explained in
      // https://github.com/google/ksp/blob/main/docs/quickstart.md#make-ide-aware-of-generated-code
      p.extensions.findByType(KotlinProjectExtension::class.java)?.sourceSets?.all { sourceSet ->
        sourceSet.kotlin.srcDirs("build/generated/ksp/${sourceSet.name}/kotlin")
      }
    }
  }
}
