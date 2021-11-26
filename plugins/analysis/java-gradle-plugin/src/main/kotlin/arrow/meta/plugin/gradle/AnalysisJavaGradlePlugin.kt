import java.util.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

public class AnalysisJavaGradlePlugin : Plugin<Project> {
  private val groupId: String = "io.arrow-kt"

  override fun apply(project: Project) {
    // get the version from the analysis.plugin.properties file
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("analysis.plugin.properties"))
    val version = properties.getProperty("analysisPluginVersion")

    project.afterEvaluate { p ->
      // add libraries
      p.dependencies.add("compileOnly", "$groupId:arrow-analysis-types:$version")
      p.dependencies.add("compileOnly", "$groupId:arrow-analysis-laws:$version")
      // the annotation processor must be present
      p.dependencies.add("annotationProcessor", "$groupId:arrow-analysis-java-plugin:$version")
      // add the plug-in which uses the result of the processor
      p.tasks.withType(JavaCompile::class.java) { task ->
        task.options.compilerArgs.addAll(
          listOf(
            "-parameters", // IMPORTANT! otherwise we have no parameter names
            "-Xplugin:ArrowAnalysisJavaPlugin"
          )
        )
      }
    }
  }
}
