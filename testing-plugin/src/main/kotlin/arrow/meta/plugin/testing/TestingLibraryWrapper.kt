package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

private const val DEFAULT_FILENAME = "Example.kt"

data class CompilationResult(
  val actualStatus: CompilationStatus,
  val log: String,
  val actualGeneratedFilePath: Path,
  val outputDirectory: File
)

internal fun compile(data: CompilationData): CompilationResult =
  compilationResultFrom(KotlinCompilation().apply {
    sources = listOf(SourceFile.kotlin("Example.kt", data.source))
    classpaths = data.dependencies.map { classpathOf(it) }
    pluginClasspaths = data.compilerPlugins.map { classpathOf(it) }
  }.compile())


private fun compilationResultFrom(internalResult: KotlinCompilation.Result): CompilationResult =
  CompilationResult(
    actualStatus = exitStatusFrom(internalResult.exitCode),
    log = internalResult.messages,
    actualGeneratedFilePath = Paths.get(internalResult.outputDirectory.parent, "sources", "$DEFAULT_FILENAME.meta"),
    outputDirectory = internalResult.outputDirectory
  )

private fun exitStatusFrom(exitCode: KotlinCompilation.ExitCode): CompilationStatus =
  when (exitCode) {
    KotlinCompilation.ExitCode.OK -> CompilationStatus.OK
    KotlinCompilation.ExitCode.INTERNAL_ERROR -> CompilationStatus.INTERNAL_ERROR
    KotlinCompilation.ExitCode.COMPILATION_ERROR -> CompilationStatus.COMPILATION_ERROR
    KotlinCompilation.ExitCode.SCRIPT_EXECUTION_ERROR -> CompilationStatus.SCRIPT_EXECUTION_ERROR
  }

private fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  val file = ClassGraph().classpathFiles.firstOrNull { classpath -> classpath.name.matches(regex) }
  assertThat(file).`as`("$dependency not found in test runtime. Check your build configuration.").isNotNull()
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}