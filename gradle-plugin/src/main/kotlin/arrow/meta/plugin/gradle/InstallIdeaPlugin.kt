package arrow.meta.plugin.gradle

import org.apache.xerces.parsers.DOMParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.xml.sax.InputSource
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties
import java.util.zip.ZipFile

private const val IDEA_PLUGIN_NAME = "Arrow Meta Intellij IDEA Plugin"

open class InstallIdeaPlugin: DefaultTask() {

  @TaskAction
  fun installPlugin() {
    if (!inIdea()) {
      println("Run this task from Intellij IDEA")
      return
    }

    if (!pluginsDirExists()) {
      println("Plugins directory not found")
      return
    }

    if (ideaPluginExists()) {
      println("Arrow Meta IDEA Plugin is already installed!")
      return
    }

    println("Arrow Meta IDEA Plugin is not installed! Downloading ...")
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))

    val pluginFileURL = getPluginFileURL(properties)
    val pluginFileName = File(pluginFileURL).name
    downloadPluginFile(pluginFileURL, pluginFileName)
    unzipPluginFile(pluginFileName)
    removePluginFile(pluginFileName)
    // TODO: dynamic plugin to avoid restarting
    println("Restart Intellij IDEA to finish the installation!")
  }

  private fun getPluginFileURL(properties: Properties): String? {
    val compilerPluginVersion = properties.getProperty("COMPILER_PLUGIN_VERSION")
    val parser = DOMParser()
    parser.parse(InputSource(URL("https://meta.arrow-kt.io/idea-plugin/$compilerPluginVersion/updatePlugins.xml").openStream()))
    return parser.document.getElementsByTagName("plugin").item(0).attributes.getNamedItem("url").nodeValue
  }

  private fun downloadPluginFile(pluginFileURL: String?, pluginFileName: String): Unit {
    Files.copy(
      URL(pluginFileURL).openStream(),
      Paths.get(pluginsDir(), pluginFileName)
    )
  }

  private fun unzipPluginFile(pluginFileName: String): Unit {
    val zipFile = ZipFile(Paths.get(pluginsDir(), pluginFileName).toFile())
    zipFile.entries().asSequence().forEach { zipEntry ->
      when {
        zipEntry.isDirectory -> Files.createDirectory(Paths.get(pluginsDir(), zipEntry.name))
        else -> Files.copy(
          zipFile.getInputStream(zipEntry),
          Paths.get(pluginsDir(), zipEntry.name)
        )
      }
    }
  }

  private fun removePluginFile(pluginFileName: String): Unit {
    Files.delete(Paths.get(pluginsDir(), pluginFileName))
  }
}

private fun pluginsDir(): String =
  when {
    System.getProperty("idea.plugins.path") != null -> System.getProperty("idea.plugins.path")
    else -> pluginsDirFromVmOptionsFile()
  }

private fun pluginsDirFromVmOptionsFile(): String {
  val configDir = File(System.getProperty("jb.vmOptionsFile")).parent
  return Paths.get(configDir, "plugins").toString()
}

internal fun inIdea(): Boolean =
  System.getProperty("idea.active") == "true"

internal fun pluginsDirExists(): Boolean =
  when {
    System.getProperty("idea.plugins.path") != null -> File(System.getProperty("idea.plugins.path")).exists()
    System.getProperty("jb.vmOptionsFile") != null -> File(pluginsDirFromVmOptionsFile()).exists()
    else -> false
  }

internal fun ideaPluginExists(): Boolean =
  File(pluginsDir()).listFiles().any { it.name == IDEA_PLUGIN_NAME }
