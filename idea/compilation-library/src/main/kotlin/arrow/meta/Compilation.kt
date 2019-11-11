package arrow.meta

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import java.io.File
import java.util.Properties

const val DEFAULT_FILENAME = "Source.kt"

class Compilation {
  fun compile(source: String): Result {
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("library.properties"))
    val arrowVersion = properties.getProperty("ARROW_VERSION")
    val actualVersion = properties.getProperty("ACTUAL_VERSION")

    return KotlinCompilation().apply {
      sources = listOf(SourceFile.kotlin(DEFAULT_FILENAME, source))
      classpaths = listOf(classpathOf("arrow-annotations:$arrowVersion"), classpathOf("arrow-core-data:$arrowVersion"))
      pluginClasspaths = listOf(classpathOf("compiler-plugin:$actualVersion:all"))
    }.compile()
  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }
}