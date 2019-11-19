package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Paths

private const val META_PREFIX = "//meta"
private const val METHOD_CALL = "[^(]+\\(\\)(\\.\\S+)?"
private const val VARIABLE = "[^(]+"

/**
 * Allows to check if a compiler plugin is working as expected.
 *
 * It's not necessary to write assertions with actual and expected behaviour.
 * Just indicating the expected behaviour in a [CompilerTest] and assertions will be built automatically
 * from it.
 *
 * For instance:
 *
 * ```
 *  assertThis(
 *      CompilerTest(
 *        config = { metaDependencies },
 *        code = { "...".source },
 *        assert = {
 *          quoteOutputMatches("...".source) +
 *          "...".source.evalsTo(someValue)
 *        }
 *      )
 *  )
 * ```
 *
 * For running the compilation from the provided configuration and getting the results (status, classes and output
 * messages) makes use of [Kotlin Compile Testing](https://github.com/tschuchortdev/kotlin-compile-testing), a library
 * developed by [Thilo Schuchort](https://github.com/tschuchortdev).
 *
 * @param compilerTest necessary data to run the compilation, source code to be compiled and expected behaviour
 * @see [CompilerTest]
 */
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

  fun runAssert(assert: Assert, compilationResult: Result): Unit = when (assert) {
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
  it.assert(CompilerTest).map { assert -> runAssert(assert, compilationResult) }
}

private fun CompilationData.addDependencies(config: Config.AddDependencies) =
  copy(dependencies = dependencies + config.dependencies.map { it.mavenCoordinates })

private fun CompilationData.addCompilerPlugins(config: Config.AddCompilerPlugins) =
  copy(compilerPlugins = compilerPlugins + config.plugins.flatMap { it.dependencies.map { it.mavenCoordinates } })

private fun assertEvalsTo(compilationResult: Result, source: Source, output: Any?) {
  assertCompiles(compilationResult)
  val expression = source.text.trimMargin()
  val classesDirectory = compilationResult.outputDirectory
  assertThat(expression)
    .`as`("EXPECTED: expressions like myVariable, myFunction() or myFunction().value - ACTUAL: $expression")
    .matches("^($VARIABLE)|($METHOD_CALL)\$")
  when {
    expression.matches(Regex("^$METHOD_CALL\$")) -> assertThat(call(expression, classesDirectory)).isEqualTo(output)
    else -> assertThat(eval(expression, classesDirectory)).isEqualTo(output)
  }
}

private fun assertCompiles(compilationResult: Result): Unit {
  assertThat(compilationResult.exitCode).isEqualTo(ExitCode.OK)
}

private fun assertFails(compilationResult: Result): Unit {
  assertThat(compilationResult.exitCode).isNotEqualTo(ExitCode.OK)
}

private fun assertFailsWith(compilationResult: Result, check: (String) -> Boolean): Unit {
  assertFails(compilationResult)
  assertThat(check(compilationResult.messages)).isTrue()
}

private fun assertQuoteOutputMatches(compilationResult: Result, expectedSource: Source): Unit {
  assertCompiles(compilationResult)
  val actualFilePath = Paths.get(compilationResult.outputDirectory.parent, "sources", "$DEFAULT_FILENAME.meta")
  val actualSource = actualFilePath.toFile().readText()
  val actualSourceWithoutCommands = removeCommands(actualSource)
  val expectedSourceWithoutCommands = removeCommands(expectedSource.text.trimMargin())

  assertThat(actualSourceWithoutCommands)
    .`as`("EXPECTED:${expectedSource.text.trimMargin()}\nACTUAL:$actualSource\nNOTE: Meta commands are skipped in the comparison")
    .isEqualToIgnoringWhitespace(expectedSourceWithoutCommands)
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun call(expression: String, classesDirectory: File): Any? {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expressionParts = expression.split("()")
  val method = expressionParts[0]
  val property = expressionParts[1].removePrefix(".")

  val resultForMethodCall: Any? = classLoader.loadClass(DEFAULT_CLASSNAME).getMethod(method).invoke(null)
  return when {
    property.isNullOrBlank() -> resultForMethodCall
    else -> resultForMethodCall?.javaClass?.getField(property)?.get(resultForMethodCall)
  }
}

private fun eval(expression: String, classesDirectory: File): Any {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val field = classLoader.loadClass(DEFAULT_CLASSNAME).getDeclaredField(expression)
  field.isAccessible = true
  return field.get(Object())
}
