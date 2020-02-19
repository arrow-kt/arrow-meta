package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.KotlinCompilation.Result
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private const val META_PREFIX = "//meta"
private const val METHOD_CALL = "[^(]+\\(\\)(\\.\\S+)?"
private const val VARIABLE = "[^(]+"

/**
 * Allows checking if a compiler plugin is working as expected.
 *
 * It's not necessary to write assertions with actual and expected behavior.
 * Assertions will be built automatically from simply indicating the expected behavior in a [CompilerTest].
 *
 * Running the compilation from the provided configuration and getting the results (status, classes, and output
 * messages) is possible thanks to [Kotlin Compile Testing](https://github.com/tschuchortdev/kotlin-compile-testing),
 * a library developed by [Thilo Schuchort](https://github.com/tschuchortdev).
 *
 * Main schema:
 *
 * ```
 *  assertThis(
 *      CompilerTest(
 *        config = { ... },
 *        code = { ... },
 *        assert = { ... }
 *      )
 *  )
 * ```
 *
 * Compilation will be executed with the provided configuration (`config`) and code snippets (`code`). Then,
 * the expected behavior (`assert`) will be checked.
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
 * @param compilerTest necessary data to run the compilation, source code to be compiled and expected behavior
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
          is Config.AddMetaPlugins -> remaining.compilationData(acc.addMetaPlugins(config))
          is Config.Many -> (config.configs + remaining).compilationData(acc)
          Config.Empty -> remaining.compilationData(acc)
        }
      }
    }

  fun Code.Source.renameBy(index: Int) =
    when (this.filename) {
      DEFAULT_FILENAME -> Code.Source(filename = this.filename.replace(".kt", "${index}.kt"), text = this.text)
      else -> this
    }

  fun Code.compilationData(): CompilationData =
    when (this) {
      is Code.Source -> CompilationData(sources = listOf(this))
      is Code.Sources -> CompilationData(sources = this.sources.mapIndexed { index, source -> source.renameBy(index) })
    }

  fun runAssert(singleAssert: Assert.SingleAssert, compilationResult: Result): Unit =
    when (singleAssert) {
      Assert.Empty -> println("Assertions not found")
      Assert.CompilationResult.Compiles -> assertCompiles(compilationResult)
      Assert.CompilationResult.Fails -> assertFails(compilationResult)
      is Assert.FailsWith -> assertFailsWith(compilationResult, singleAssert.f)
      is Assert.QuoteOutputMatches -> assertQuoteOutputMatches(compilationResult, singleAssert.source)
      is Assert.EvalsTo -> assertEvalsTo(compilationResult, singleAssert.source, singleAssert.output)
      is Assert.QuoteFileMatches -> assertQuoteFileMatches(compilationResult, singleAssert.filename, singleAssert.source)
      else -> TODO()
    }

  val initialCompilationData = it.code(CompilerTest).compilationData()
  val compilationData = it.config(CompilerTest).compilationData(initialCompilationData)
  val compilationResult = compile(compilationData)
  val assert = it.assert(CompilerTest)
  when (assert) {
    is Assert.SingleAssert -> runAssert(assert, compilationResult)
    is Assert.Many -> assert.asserts.map { singleAssert -> runAssert(singleAssert, compilationResult) }
  }
}

private fun CompilationData.addDependencies(config: Config.AddDependencies) =
  copy(dependencies = dependencies + config.dependencies.map { it.mavenCoordinates })

private fun CompilationData.addCompilerPlugins(config: Config.AddCompilerPlugins) =
  copy(compilerPlugins = compilerPlugins + config.plugins.flatMap { it.dependencies.map { it.mavenCoordinates } })

private fun CompilationData.addMetaPlugins(config: Config.AddMetaPlugins) =
  copy(metaPlugins = metaPlugins + config.plugins)

private fun assertEvalsTo(compilationResult: Result, source: Code.Source, output: Any?) {
  assertCompiles(compilationResult)
  val className = source.filename.replace(".kt", "Kt")
  val expression = source.text.trimMargin()
  val classesDirectory = compilationResult.outputDirectory
  assertThat(expression)
    .`as`("EXPECTED: expressions like myVariable, myFunction() or myFunction().value - ACTUAL: $expression")
    .matches("^($VARIABLE)|($METHOD_CALL)\$")
  when {
    expression.matches(Regex("^$METHOD_CALL\$")) -> assertThat(call(className, expression, classesDirectory)).isEqualTo(output)
    else -> assertThat(eval(className, expression, classesDirectory)).isEqualTo(output)
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

private fun assertQuoteOutputMatches(compilationResult: Result, expectedSource: Code.Source): Unit = assertQuoteFileMatches(
  compilationResult = compilationResult,
  expectedSource = expectedSource,
  actualFileName = "$DEFAULT_FILENAME.meta",
  actualFileDirectoryPath = Paths.get(compilationResult.outputDirectory.parent, "sources")
)

private fun assertQuoteFileMatches(
  compilationResult: Result,
  actualFileName: String,
  expectedSource: Code.Source,
  actualFileDirectoryPath: Path = Paths.get("", *System.getProperty("arrow.meta.generated.source.output").split("/").toTypedArray())
): Unit {
  assertCompiles(compilationResult)
  val actualSource = actualFileDirectoryPath.resolve(actualFileName).toFile().readText()
  val actualSourceWithoutCommands = removeCommands(actualSource)
  val expectedSourceWithoutCommands = removeCommands(expectedSource.text.trimMargin())
  assertThat(actualSourceWithoutCommands)
    .`as`("EXPECTED:${expectedSource.text.trimMargin()}\nACTUAL:$actualSource\nNOTE: Meta commands are skipped in the comparison")
    .isEqualToIgnoringWhitespace(expectedSourceWithoutCommands)
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun call(className: String, expression: String, classesDirectory: File): Any? {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expressionParts = expression.split("()")
  val method = expressionParts[0]
  val property = expressionParts[1].removePrefix(".")

  val fullClassName = getFullClassName(classesDirectory, className)
  val resultForMethodCall: Any? = classLoader.loadClass(fullClassName).getMethod(method).invoke(null)
  return when {
    property.isNullOrBlank() -> resultForMethodCall
    else -> resultForMethodCall?.javaClass?.getMethod("get${property.capitalize()}")?.invoke(resultForMethodCall)
  }
}

private fun eval(className: String, expression: String, classesDirectory: File): Any? {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val fullClassName = getFullClassName(classesDirectory, className)
  val field = classLoader.loadClass(fullClassName).getDeclaredField(expression)
  field.isAccessible = true
  return field.get(Object())
}

private fun getFullClassName(classesDirectory: File, className: String): String =
  Files.walk(Paths.get(classesDirectory.toURI())).filter { it.toFile().name == "$className.class" }.toArray()[0].toString()
    .removePrefix(classesDirectory.absolutePath + File.separator)
    .removeSuffix(".class")
    .replace(File.separator, ".")
