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

fun assertThis(compilerTest: CompilerTest): Unit =
  compilerTest.run(interpreter)

private val interpreter: (CompilerTest) -> Unit = {

  tailrec fun List<Config>.compilationData(acc: CompilationData = CompilationData.empty): CompilationData =
    when {
      isEmpty() -> acc
      else -> {
        val (config, tail) = this[0] to drop(1)
        when (config) {
          is Config.AddCompilerPlugins ->
            tail.compilationData(acc.copy(compilerPlugins = acc.compilerPlugins + config.plugins.flatMap { it.dependencies.map { it.mavenCoordinates } }))
          is Config.AddDependencies ->
            tail.compilationData(acc.copy(dependencies = acc.dependencies + config.dependencies.map { it.mavenCoordinates }))
          is Config.Many ->
            (config.configs + tail).compilationData(acc)
          Config.Empty ->
            tail.compilationData(acc)
          is Config.AddMetaPlugins ->
            TODO("How do we bootstrap the Meta Plugin Quote system?")
        }
      }
    }

  fun runAssert(assert: Assert, compilationResult: CompilationResult): Unit = when (assert) {
    Assert.Empty -> println("Assertions not found")
    Assert.Compiles -> assertCompiles(compilationResult)
    Assert.Fails -> assertFails(compilationResult)
    is Assert.FailsWith -> assertFailsWith(compilationResult, assert.f)
    is Assert.QuoteOutputMatches -> assertQuoteOutputMatches(compilationResult, assert.source)
    is Assert.EvalsTo -> assertEvalsTo(compilationResult, assert.source, assert.output)
  }

  val initialCompilationData = CompilationData(source = listOf(it.code(CompilerTest).text.trimMargin()))
  val compilationData = it.config(CompilerTest).compilationData(initialCompilationData)
  val compilationResult = compile(compilationData)
  runAssert(it.assert(CompilerTest), compilationResult)
}
fun compilationResult(c: CompilerTest): CompilationResult {
  fun runConfig(config: Config): Unit = when (config) {
    is Config.AddCompilerPlugins -> println("Plugins found: ${config.plugins}")
    is Config.AddDependencies -> println("Dependencies found: ${config.dependencies}")
    is Config.Many -> config.configs.forEach(::runConfig)
    Config.Empty -> println("Testing configuration not found")
  }

  val configuration = c.config(CompilerTest)

  configuration.map(::runConfig)

  val compilerPlugins = configuration.filterIsInstance<Config.Many>().flatMap { it.configs }.filterIsInstance<Config.AddCompilerPlugins>().flatMap { it.plugins }.flatMap { it.dependencies }.map { it.mavenCoordinates }
  val dependencies = configuration.filterIsInstance<Config.Many>().flatMap { it.configs }.filterIsInstance<Config.AddDependencies>().flatMap { it.dependencies }.map { it.mavenCoordinates }
  val compilationData = CompilationData(dependencies = dependencies, source = c.code(CompilerTest).trimMargin(), compilerPlugins = compilerPlugins)
  return compile(compilationData)
}

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
  val expectedSourceWithoutCommands = removeCommands(expectedSource.trimMargin())

  assertThat(actualSourceWithoutCommands)
    .`as`("EXPECTED:${expectedSource.trimMargin()}\nACTUAL:$actualSource\nNOTE: Meta commands are skipped in the comparison")
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