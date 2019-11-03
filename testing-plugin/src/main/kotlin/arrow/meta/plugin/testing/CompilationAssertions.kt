package arrow.meta.plugin.testing

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.net.URLClassLoader

private const val META_PREFIX = "//meta"
private const val EXPRESSION_PATTERN = "^[^(]+\\(\\)(\\.\\S+)?\$"
private const val DEFAULT_CLASSNAME = "ExampleKt"

private data class ExpressionParts(
  val method: String,
  val property: String? = null
)

data class CompilationAssertions<A>(val f: () -> A)

fun assertThis(compilerTest: CompilerTest): CompilationAssertions<Unit> =
  compilerTest.run(::interpreter)

fun acquire(src: String, config: CompilerTest.Companion.() -> List<Config>): CompilationResource =
  compile(config(CompilerTest.Companion).compilationData(CompilationData(source = listOf(src)))).run {
    CompilationResource(actualStatus, classesDirectory, generatedFiles)
  }

private fun interpreter(test: CompilerTest): CompilationAssertions<Unit> {

  fun runAssert(assert: Assert, compilationResult: CompilationResult): Unit = when (assert) {
    Assert.Empty -> println("Assertions not found")
    Assert.CompilationResult.Compiles -> assertCompiles(compilationResult)
    Assert.CompilationResult.Fails -> assertFails(compilationResult)
    is Assert.FailsWith -> assertFailsWith(compilationResult, assert.f)
    is Assert.QuoteOutputMatches -> assertQuoteOutputMatches(compilationResult, assert.source)
    is Assert.EvalsTo -> assertEvalsTo(compilationResult, assert.source, assert.output)
  }

  val initialCompilationData = CompilationData(source = listOf(test.code(CompilerTest).text.trimMargin()))
  val compilationData = test.config(CompilerTest).compilationData(initialCompilationData)
  val compilationResult = compile(compilationData)
  return CompilationAssertions { runAssert(test.assert(CompilerTest), compilationResult) }
}

internal tailrec fun List<Config>.compilationData(acc: CompilationData = CompilationData.empty): CompilationData =
  when {
    isEmpty() -> acc
    else -> {
      val (config, remaining) = this[0] to drop(1)
      when (config) {
        is Config.AddCompilerPlugins -> remaining.compilationData(acc.addCompilerPlugins(config))
        is Config.AddDependencies -> remaining.compilationData(acc.addDependencies(config))
        is Config.Many -> (config.configs + remaining).compilationData(acc)
        Config.Empty -> remaining.compilationData(acc)
        is Config.AddMetaPlugins -> TODO("How do we bootstrap the Meta Plugin Quote system?")
      }
    }
  }

private fun CompilationData.addDependencies(config: Config.AddDependencies) =
  copy(dependencies = dependencies + config.dependencies.map { it.mavenCoordinates })

private fun CompilationData.addCompilerPlugins(config: Config.AddCompilerPlugins) =
  copy(compilerPlugins = compilerPlugins + config.plugins.flatMap { it.dependencies.map { it.mavenCoordinates } })

private fun assertEvalsTo(compilationResult: CompilationResult, source: Source, output: Any?) {
  assertCompiles(compilationResult)
  assertThat(source.text.trimMargin()).matches(EXPRESSION_PATTERN)
  assertThat(call(source.text.trimMargin(), compilationResult.classesDirectory)).isEqualTo(output)
}

private fun assertCompiles(compilationResult: CompilationResult): Unit {
  assertThat(compilationResult.actualStatus).isEqualTo(CompilationStatus.OK)
}

private fun assertFails(compilationResult: CompilationResult): Unit {
  assertThat(compilationResult.actualStatus).isNotEqualTo(CompilationStatus.OK)
}

private fun assertFailsWith(compilationResult: CompilationResult, check: (String) -> Boolean): Unit {
  assertFails(compilationResult)
  assertThat(check(compilationResult.log)).isTrue()
}

private fun assertQuoteOutputMatches(compilationResult: CompilationResult, expectedSource: Source): Unit {
  assertCompiles(compilationResult)
  val actualSource = compilationResult.actualGeneratedFilePath.toFile().readText()
  val actualSourceWithoutCommands = removeCommands(actualSource)
  val expectedSourceWithoutCommands = removeCommands(expectedSource.text.trimMargin())

  assertThat(actualSourceWithoutCommands)
    .`as`("EXPECTED:${expectedSource.text.trimMargin()}\nACTUAL:$actualSource\nNOTE: Meta commands are skipped in the comparison")
    .isEqualToIgnoringWhitespace(expectedSourceWithoutCommands)
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun eval(expression: String): ExpressionParts {
  val parts = expression.split("()")
  return when {
    parts.size > 1 -> ExpressionParts(method = parts[0], property = parts[1].removePrefix("."))
    else -> ExpressionParts(method = parts[0])
  }
}

private fun call(expression: String, classesDirectory: File): Any {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expressionParts = eval(expression)

  val resultForMethodCall = classLoader.loadClass(DEFAULT_CLASSNAME).getMethod(expressionParts.method).invoke(null)
  return when {
    expressionParts.property.isNullOrBlank() -> resultForMethodCall
    else -> resultForMethodCall.javaClass.getField(expressionParts.property).get(resultForMethodCall)
  }
}