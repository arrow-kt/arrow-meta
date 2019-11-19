package arrow.meta.plugin.testing

import arrow.meta.Plugin

/**
 * Represents all kind of code snippets: source code to be compiled, expressions, etc.
 * Avoid the use of a primitive type.
 */
data class Source(val text: String)

/**
 * Represents a dependency from `<artifact-id>:<version>` string.
 */
data class Dependency(val mavenCoordinates: String)

/**
 * Represents a compiler plugin.
 */
data class CompilerPlugin(

  /**
   * Descriptive name about the Compiler Plugin.
   */
  val name: String,

  /**
   * List of necessary dependencies to use that plugin during the compilation.
   */
  val dependencies: List<Dependency>
)

internal typealias CompilerTestInterpreter = (CompilerTest) -> Unit

/**
 * Allows to indicate the expected behaviour when testing plugins.
 *
 * @see [assertThis]
 */
data class CompilerTest(
  /**
   * Necessary configuration to run the compilation: compiler plugins and dependencies.
   *
   * @see [Config]
   */
  val config: Companion.() -> List<Config> = { emptyList() },
  /**
   * Code snippet which will be compiled.
   */
  val code: Companion.() -> Source, // TODO: Sources
  /**
   * Expected behaviour during and after compilation.
   *
   * @see [Assert]
   */
  val assert: Companion.() -> List<Assert> = { emptyList() }
) {
  internal fun run(interpret: CompilerTestInterpreter): Unit =
    interpret(this)

  companion object : ConfigSyntax by Config, AssertSyntax by Assert {
    operator fun invoke(f: Companion.() -> CompilerTest): CompilerTest =
      f(this)
  }
}

/**
 * Allows to indicate the necessary configuration to run a compilation.
 *
 * @see [CompilerTest]
 */
interface ConfigSyntax {
  val emptyConfig: Config

  /**
   * Adds the compiler plugins to run the compilation.
   */
  fun addCompilerPlugins(vararg element: CompilerPlugin): Config =
    Config.Many(listOf(Config.AddCompilerPlugins(element.toList())))

  /**
   * Adds the Meta Plugins to run the compilation.
   */
  fun addMetaPlugins(vararg element: Plugin): Config =
    Config.Many(listOf(Config.AddMetaPlugins(element.toList())))

  /**
   * Adds the necessary dependencies to run the compilation.
   */
  fun addDependencies(vararg element: Dependency): Config =
    Config.Many(listOf(Config.AddDependencies(element.toList())))

  /**
   * Allows to combine [Config].
   */
  operator fun Config.plus(other: Config): List<Config> =
    listOf(this, other)

  private fun prelude(currentVersion: String?): Dependency =
    Dependency("prelude:$currentVersion")

  /**
   * Simplifies the configuration with a default configuration: Arrow Meta Compiler Plugin + Arrow Annotations as
   * a dependency.
   */
  val metaDependencies: List<Config>
    get() {
      val currentVersion = System.getProperty("CURRENT_VERSION")
      val arrowVersion = System.getProperty("ARROW_VERSION")
      val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin:$currentVersion:all")))
      val arrowAnnotations = Dependency("arrow-annotations:$arrowVersion")
      return CompilerTest.addCompilerPlugins(compilerPlugin) + CompilerTest.addDependencies(arrowAnnotations) + CompilerTest.addDependencies(prelude(currentVersion))
    }
}

/**
 * Represents the different types of [Config] which will be managed.
 */
sealed class Config {
  internal data class AddCompilerPlugins(val plugins: List<CompilerPlugin>) : Config()
  internal data class AddMetaPlugins(val plugins: List<Plugin>) : Config()
  internal data class AddDependencies(val dependencies: List<Dependency>) : Config()
  internal data class Many(val configs: List<Config>) : Config()
  internal object Empty : Config()

  internal companion object : ConfigSyntax {
    override val emptyConfig: Config = Config.emptyConfig
  }
}

/**
 * Provides expected behaviours.
 *
 * @see [CompilerTest.assert]
 */
interface AssertSyntax {
  val emptyAssert: Assert

  /**
   * Checks that code snippet compiles successfully.
   */
  val compiles: Assert

  /**
   * Checks that code snippet fails.
   */
  val fails: Assert

  /**
   * Checks both that code snippet fails and the error message has a certain property which is
   * checked by a provided function.
   *
   * For instance:
   *
   * ```
   * failsWith { it.contains("Expecting a top level declaration") }
   * ```
   *
   * @param f function that must return true from the error message as an input.
   */
  fun failsWith(f: (String) -> Boolean): Assert = Assert.FailsWith(f)

  /**
   * Checks that quote output during the compilation matches with the code snippet provided.
   *
   * @param source Code snippet with the expected quote output.
   */
  fun quoteOutputMatches(source: Source): Assert = Assert.QuoteOutputMatches(source)

  /**
   * Checks if a code snippet evals to a provided value after the compilation.
   * This operation loads all the generated classes and run the code snippet by reflection.
   *
   * @param value Expected result after running the code snippet.
   */
  infix fun Source.evalsTo(value: Any?): Assert = Assert.EvalsTo(this, value)

  /**
   * Returns a Source object from a String.
   */
  val String.source: Source get() = Source(this)

  /**
   * Allows to combine [Assert].
   */
  operator fun Assert.plus(other: Assert): List<Assert> =
    listOf(this, other)

  /**
   * Creates a list of asserts.
   */
  fun allOf(vararg elements: Assert): List<Assert> =
    if (elements.isNotEmpty()) elements.asList() else emptyList()
}

/**
 * Represents the different types of [Assert] which will be managed.
 */
sealed class Assert {
  internal sealed class CompilationResult : Assert() {
    object Compiles : CompilationResult()
    object Fails : CompilationResult()
  }

  internal object Empty : Assert()
  internal data class QuoteOutputMatches(val source: Source) : Assert()
  internal data class EvalsTo(val source: Source, val output: Any?) : Assert()
  internal data class FailsWith(val f: (String) -> Boolean) : Assert()

  internal companion object : AssertSyntax {
    override val emptyAssert: Assert = Empty
    override val compiles: Assert = CompilationResult.Compiles
    override val fails: Assert = CompilationResult.Fails
  }
}