package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.io.PrintStream

internal const val DEFAULT_FILENAME = "Source.kt"

internal fun compile(data: CompilationData): Result =
  KotlinCompilation().apply {
    sources = data.sources.map { SourceFile.kotlin(it.filename, it.text.trimMargin()) }
    classpaths = data.dependencies.map { classpathOf(it) }
    pluginClasspaths = data.compilerPlugins.map { classpathOf(it) }
    compilerPlugins = data.metaPlugins
    messageOutputStream = object : PrintStream(System.out) {
      override fun write(buf: ByteArray, off: Int, len: Int) {
        val newLine = String(buf, off, len)
          .run { replace(Regex("^e:"), "error found:") }
          .toByteArray()

        super.write(newLine, off, newLine.size)
      }
    }
  }.compile()

private fun rewriteAndPrintOutput(lines: List<String>) {
  println(lines.joinToString(System.lineSeparator()) { it.replace(Regex("^e:"), "error found:") })
}

private fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  val file = ClassGraph().classpathFiles.firstOrNull { classpath -> classpath.name.matches(regex) }
  assertThat(file).`as`("$dependency not found in test runtime. Check your build configuration.").isNotNull()
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}