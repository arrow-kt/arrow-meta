package arrow.meta.plugin.gradle

import com.sun.org.apache.xerces.internal.parsers.DOMParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import io.github.classgraph.ClassGraph
import org.xml.sax.InputSource
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
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

    project.extensions.create("arrow", ArrowExtension::class.java)
    project.afterEvaluate { p ->
      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += "-Xplugin=${classpathOf("compiler-plugin:$compilerPluginVersion")}"
      }
    }

    // TODO: Do the same for release version
    when {
      (System.getProperty("idea.active") == "true") -> {
        val configDir = File(System.getProperty("jb.vmOptionsFile")).parent
        val pluginsDir = Paths.get(configDir, "plugins")
        if (pluginsDir.toFile().listFiles().none { it.name.startsWith("idea-plugin") }) {
          println("Arrow Meta IDEA Plugin is not installed! Downloading ...")
          val parser = DOMParser()
          parser.parse(InputSource(URL("https://meta.arrow-kt.io/idea-plugin/snapshots/$compilerPluginVersion/updatePlugins.xml").openStream()))
          val artifactURL = parser.document.getElementsByTagName("plugin").item(0).attributes.getNamedItem("url").nodeValue
          Files.copy(
            URL(artifactURL).openStream(),
            Paths.get(pluginsDir.toString(), File(artifactURL).name)
          )
          // TODO: dynamic plugin to avoid restarting
          println("Restart Intellij IDEA to finish the installation!")
        }
      }
    }

  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}
