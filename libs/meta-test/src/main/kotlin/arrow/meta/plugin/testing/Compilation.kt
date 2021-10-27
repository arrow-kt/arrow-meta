@file:OptIn(ExperimentalStdlibApi::class)

package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import java.io.File
import java.io.PrintStream
import org.assertj.core.api.Assertions.assertThat

internal const val DEFAULT_FILENAME = "Source.kt"

internal fun compile(data: CompilationData): Result =
  KotlinCompilation().apply {
    val testSources = workingDir.resolve("sources")
    System.setProperty("arrow.meta.generate.source.dir", testSources.absolutePath)
    sources = data.sources.map { SourceFile.kotlin(it.filename, it.text.trimMargin()) }
    classpaths = data.dependencies.map { classpathOf(it) }
    pluginClasspaths = data.compilerPlugins.map { classpathOf(it) }
    compilerPlugins = data.metaPlugins
    jvmTarget = obtainTarget(data)
    messageOutputStream = object : PrintStream(System.out) {

      private val kotlincErrorRegex = Regex("^e:")

      override fun write(buf: ByteArray, off: Int, len: Int) {
        val newLine = String(buf, off, len)
          .run { replace(kotlincErrorRegex, "error found:") }
          .toByteArray()

        super.write(newLine, off, newLine.size)
      }
    }
    kotlincArguments = data.arguments
    commandLineProcessors = data.commandLineProcessors
    pluginOptions = data.pluginOptions.map { PluginOption(it.pluginId, it.key, it.value) }
  }.compile()

private fun obtainTarget(data: CompilationData): String =
  data.targetVersion ?: System.getProperty("JVM_TARGET_VERSION", "1.8")

private fun classpathOf(dependency: String): File {
  val file =
    ClassGraph().classpathFiles.firstOrNull { classpath ->
      dependenciesMatch(classpath, dependency)
    }
  println("classpath: ${ClassGraph().classpathFiles}")
  assertThat(file).`as`("$dependency not found in test runtime. Check your build configuration.").isNotNull
  return file!!
}

private fun dependenciesMatch(classpath: File, dependency: String): Boolean {
  val dep = classpath.name
  val dependencyName = sanitizeClassPathFileName(dep)
  val testdep = dependency.substringBefore(":")
  return testdep == dependencyName
}

private fun sanitizeClassPathFileName(dep: String): String =
  buildList<Char> {
    var skip = false
    add(dep.first())
    dep.reduce { a, b ->
      if (a == '-' && b.isDigit()) skip = true
      if (!skip) add(b)
      b
    }
    if (skip) removeLast()
  }.joinToString("")
    .replace("-jvm.jar", "")
    .replace("-jvm", "")


