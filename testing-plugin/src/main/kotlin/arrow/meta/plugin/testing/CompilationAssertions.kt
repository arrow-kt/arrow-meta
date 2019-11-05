package arrow.meta.plugin.testing

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.net.URLClassLoader

private const val META_PREFIX = "//meta"
private const val METHOD_CALL = "[^(]+\\(\\)(\\.\\S+)?"
private const val VARIABLE = "[^(]+"
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

  fun runAssert(assert: Assert, compilationResult: CompilationResult): Unit = when (assert) {
    Assert.Empty -> println("Assertions not found")
    Assert.CompilationResult.Compiles -> assertCompiles(compilationResult)
    Assert.CompilationResult.Fails -> assertFails(compilationResult)
    is Assert.FailsWith -> assertFailsWith(compilationResult, assert.f)
    is Assert.QuoteOutputMatches -> assertQuoteOutputMatches(compilationResult, assert.source)
    is Assert.EvalsTo -> assertEvalsTo(compilationResult, assert.source, assert.output)
  }

  val initialCompilationData = CompilationData(source = listOf(it.code(CompilerTest).text.trimMargin()))
  val compilationData = it.config(CompilerTest).compilationData(initialCompilationData)
  val compilationResult = compile(compilationData)
  runAssert(it.assert(CompilerTest), compilationResult)
}

private fun CompilationData.addDependencies(config: Config.AddDependencies) =
  copy(dependencies = dependencies + config.dependencies.map { it.mavenCoordinates })

private fun CompilationData.addCompilerPlugins(config: Config.AddCompilerPlugins) =
  copy(compilerPlugins = compilerPlugins + config.plugins.flatMap { it.dependencies.map { it.mavenCoordinates } })

private fun assertEvalsTo(compilationResult: CompilationResult, source: Source, output: Any?) {
  assertCompiles(compilationResult)
  val expression = source.text.trimMargin()
  assertThat(expression)
    .`as`("EXPECTED: expressions like myVariable, myFunction() or myFunction().value - ACTUAL: $expression")
    .matches("^($VARIABLE)|($METHOD_CALL)\$")
  if (expression.matches(Regex("^$METHOD_CALL\$")))
    assertThat(call(expression, compilationResult.classesDirectory)).isEqualTo(output)
  else
    assertThat(eval(expression, compilationResult.classesDirectory)).isEqualTo(output)
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

private fun partsFrom(expression: String): ExpressionParts {
  val parts = expression.split("()")
  return when {
    parts.size > 1 -> ExpressionParts(method = parts[0], property = parts[1].removePrefix("."))
    else -> ExpressionParts(method = parts[0])
  }
}

private fun call(expression: String, classesDirectory: File): Any {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expressionParts = partsFrom(expression)

  val resultForMethodCall = classLoader.loadClass(DEFAULT_CLASSNAME).getMethod(expressionParts.method).invoke(null)
  return when {
    expressionParts.property.isNullOrBlank() -> resultForMethodCall
    else -> resultForMethodCall.javaClass.getField(expressionParts.property).get(resultForMethodCall)
  }
}

private fun eval(expression: String, classesDirectory: File): Any {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val field = classLoader.loadClass(DEFAULT_CLASSNAME).getDeclaredField(expression)
  field.isAccessible = true
  return field.get(Object())
}