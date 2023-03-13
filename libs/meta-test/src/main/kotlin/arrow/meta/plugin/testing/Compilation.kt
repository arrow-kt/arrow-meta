package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.github.classgraph.ClassGraph
import java.io.File
import java.io.PrintStream
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

internal const val DEFAULT_FILENAME = "Source.kt"

internal fun compile(data: CompilationData): Result {
  val compilation = createKotlinCompilation(data)
  if (data.symbolProcessors.isEmpty()) {
    return compilation.compile()
  } else {
    // fix problems with double compilation and KSP
    // as stated in https://github.com/tschuchortdev/kotlin-compile-testing/issues/72
    val pass1 = compilation.compile()
    // if the first pass was unsuccessful, return it
    if (pass1.exitCode != KotlinCompilation.ExitCode.OK) return pass1
    // return the results of second pass
    return createKotlinCompilation(data)
      .apply {
        sources = compilation.sources + compilation.kspGeneratedSourceFiles
        symbolProcessorProviders = emptyList()
      }
      .compile()
  }
}

@OptIn(ExperimentalCompilerApi::class)
private fun createKotlinCompilation(data: CompilationData) =
  KotlinCompilation().apply {
    val testSources = workingDir.resolve("sources")
    System.setProperty("arrow.meta.generate.source.dir", testSources.absolutePath)
    sources = data.sources.map { SourceFile.kotlin(it.filename, it.text.trimMargin()) }
    classpaths = data.dependencies.map { classpathOf(it) }
    pluginClasspaths = data.compilerPlugins.map { classpathOf(it) }
    compilerPluginRegistrars = data.metaPlugins
    jvmTarget = obtainTarget(data)
    messageOutputStream =
      object : PrintStream(System.out) {

        private val kotlincErrorRegex = Regex("^e:")

        override fun write(buf: ByteArray, off: Int, len: Int) {
          val newLine =
            String(buf, off, len).run { replace(kotlincErrorRegex, "error found:") }.toByteArray()

          super.write(newLine, off, newLine.size)
        }
      }
    kotlincArguments = data.arguments
    commandLineProcessors = data.commandLineProcessors
    symbolProcessorProviders = data.symbolProcessors
    pluginOptions = data.pluginOptions.map { PluginOption(it.pluginId, it.key, it.value) }
  }

private val KotlinCompilation.kspGeneratedSourceFiles: List<SourceFile>
  get() =
    kspSourcesDir
      .resolve("kotlin")
      .walk()
      .filter { it.isFile }
      .map { SourceFile.fromPath(it.absoluteFile) }
      .toList()

private fun obtainTarget(data: CompilationData): String =
  data.targetVersion ?: System.getProperty("jvmTargetVersion", "1.8")

private fun classpathOf(dependency: String): File {
  val file =
    ClassGraph().classpathFiles.firstOrNull { classpath ->
      dependenciesMatch(classpath, dependency)
    }
  println("classpath: ${ClassGraph().classpathFiles}")
  assertThat(file)
    .`as`("$dependency not found in test runtime. Check your build configuration.")
    .isNotNull
  return file!!
}

private fun dependenciesMatch(classpath: File, dependency: String): Boolean {
  val dep = classpath.name
  val dependencyName = sanitizeClassPathFileName(dep)
  val testdep = dependency.substringBefore(":")
  return testdep == dependencyName
}

private fun sanitizeClassPathFileName(dep: String): String =
  buildList {
      var skip = false
      add(dep.first())
      dep.reduce { a, b ->
        if (a == '-' && b.isDigit()) skip = true
        if (!skip) add(b)
        b
      }
      if (skip) removeLast()
    }
    .joinToString("")
    .replace("-jvm.jar", "")
    .replace("-jvm", "")
