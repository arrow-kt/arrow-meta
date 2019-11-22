package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions.assertThat
import java.io.File

internal const val DEFAULT_FILENAME = "Source.kt"

internal fun compile(data: CompilationData): Result =
  KotlinCompilation().apply {
    sources = data.sources.map { SourceFile.kotlin(it.filename, it.text.trimMargin()) }
    classpaths = data.dependencies.map { classpathOf(it) }
    pluginClasspaths = data.compilerPlugins.map { classpathOf(it) }
    compilerPlugins = data.metaPlugins
  }.compile()

private fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  val file = ClassGraph().classpathFiles.firstOrNull { classpath -> classpath.name.matches(regex) }
  assertThat(file).`as`("$dependency not found in test runtime. Check your build configuration.").isNotNull()
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}