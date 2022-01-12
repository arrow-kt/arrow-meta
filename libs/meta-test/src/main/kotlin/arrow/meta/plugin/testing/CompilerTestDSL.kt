package arrow.meta.plugin.testing

import arrow.meta.Meta
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

/** Represents a dependency from `<artifact-id>:<version>` string. */
data class Dependency(val mavenCoordinates: String)

/** Represents a compiler plugin. */
data class CompilerPlugin(

  /** Descriptive name about the Compiler Plugin. */
  val name: String,

  /** List of necessary dependencies to use that plugin during the compilation. */
  val dependencies: List<Dependency>
)

internal typealias CompilerTestInterpreter = (CompilerTest) -> Unit

/**
 * Allows to provide configuration and code for the compilation and to indicate the expected
 * behaviour.
 *
 * @see [assertThis]
 */
data class CompilerTest(
  /**
   * Necessary configuration to run the compilation.
   *
   * @see [ConfigSyntax]
   */
  val config: Companion.() -> List<Config> = { emptyList() },
  /**
   * Code snippet o snippets which will be compiled.
   *
   * @see [CodeSyntax]
   */
  val code: Companion.() -> Code,
  /**
   * Expected behaviour during and after compilation.
   *
   * @see [AssertSyntax]
   */
  val assert: Companion.() -> Assert
) {
  internal fun run(interpret: CompilerTestInterpreter): Unit = interpret(this)

  companion object : ConfigSyntax by Config, CodeSyntax by Code, AssertSyntax by Assert {
    operator fun invoke(f: Companion.() -> CompilerTest): CompilerTest = f(this)
  }
}

data class PluginOption(val pluginId: String, val key: String, val value: String)

/**
 * Allows to indicate the necessary configuration to run a compilation.
 *
 * @see [CompilerTest]
 */
interface ConfigSyntax {
  val emptyConfig: Config

  /** Adds the compiler plugins to run the compilation. */
  fun addCompilerPlugins(vararg element: CompilerPlugin): Config =
    Config.Many(listOf(Config.AddCompilerPlugins(element.toList())))

  /** Adds the Meta Plugins to run the compilation. */
  fun addMetaPlugins(vararg element: Meta): Config =
    Config.Many(listOf(Config.AddMetaPlugins(element.toList())))

  /** Adds the necessary dependencies to run the compilation. */
  fun addDependencies(vararg element: Dependency): Config =
    Config.Many(listOf(Config.AddDependencies(element.toList())))

  /** Adds the necessary arguments to run the compilation. */
  fun addArguments(vararg element: String): Config =
    Config.Many(listOf(Config.AddArguments(element.toList())))

  /** Adds command line processors for the compiler plugins. */
  fun addCommandLineProcessors(vararg element: CommandLineProcessor): Config =
    Config.Many(listOf(Config.AddCommandLineProcessors(element.toList())))

  /** Adds KSP symbol processors. */
  fun addSymbolProcessors(vararg element: SymbolProcessorProvider): Config =
    Config.Many(listOf(Config.AddSymbolProcessors(element.toList())))

  /** Adds options for the compiler plugins. */
  fun addPluginOptions(vararg element: PluginOption): Config =
    Config.Many(listOf(Config.AddPluginOptions(element.toList())))

  /** Allows to combine [Config]. */
  operator fun Config.plus(other: Config): List<Config> = listOf(this, other)

  fun analysisLib(currentVersion: String?): Dependency =
    Dependency("arrow-analysis-types:$currentVersion")

  /**
   * Simplifies the configuration with a default configuration: Arrow Meta Compiler Plugin + Arrow
   * as a dependency.
   */
  val metaDependencies: List<Config>
    get() {
      val currentVersion = System.getProperty("CURRENT_VERSION")
      val compilerPlugin =
        CompilerPlugin("Arrow Meta", listOf(Dependency("arrow-meta:$currentVersion")))
      return listOf(addCompilerPlugins(compilerPlugin))
    }
}

/** Represents the different types of [Config] which will be managed. */
sealed class Config {
  internal data class AddCompilerPlugins(val plugins: List<CompilerPlugin>) : Config()
  internal data class AddMetaPlugins(val plugins: List<Meta>) : Config()
  internal data class AddDependencies(val dependencies: List<Dependency>) : Config()
  internal data class AddArguments(val arguments: List<String>) : Config()
  internal data class AddCommandLineProcessors(
    val commandLineProcessors: List<CommandLineProcessor>
  ) : Config()
  internal data class AddPluginOptions(val pluginOptions: List<PluginOption>) : Config()
  internal data class AddSymbolProcessors(val symbolProcessors: List<SymbolProcessorProvider>) :
    Config()
  internal data class Many(val configs: List<Config>) : Config()
  internal object Empty : Config()

  internal companion object : ConfigSyntax {
    override val emptyConfig: Config = Config.emptyConfig
  }
}

interface CodeSyntax {
  val emptyCode: Code

  /**
   * Allows to indicate several sources to be compiled.
   *
   * @see [CompilerTest]
   */
  fun sources(vararg sources: Code.Source): Code = sources(sources.toList())

  fun sources(sources: List<Code.Source>): Code = Code.Sources(sources)
}

/** Represents the different types of [Code] which will be managed. */
sealed class Code {

  /** Represents all kind of code snippets: source code to be compiled, expressions, etc. */
  data class Source(
    /** Necessary filename to identify different code snippets. */
    val filename: String = DEFAULT_FILENAME,
    /** Content of code snippet. */
    val text: String
  ) : Code()

  /** It's possible to provide one or several sources to be compiled */
  internal data class Sources(val sources: List<Source>) : Code()

  internal companion object : CodeSyntax {
    override val emptyCode: Code = Code.emptyCode
  }
}

/**
 * Allows to provide expected behaviours.
 *
 * @see [CompilerTest.assert]
 */
interface AssertSyntax {
  val emptyAssert: Assert.SingleAssert

  /** Checks that code snippet compiles successfully. */
  val compiles: Assert.SingleAssert

  /**
   * Checks that code snippet compiles successfully and (possible) warning message has a certain
   * property.
   *
   * For instance:
   *
   * ```
   * compilesWith { it.contains("Unsafe operation") }
   * ```
   *
   * @param f function that must return true from the warning message as an input.
   */
  fun compilesWith(f: (String) -> Boolean): Assert.SingleAssert = Assert.CompilesWith(f)

  /** Checks that code snippet fails. */
  val fails: Assert.SingleAssert

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
  fun failsWith(f: (String) -> Boolean): Assert.SingleAssert = Assert.FailsWith(f)

  /**
   * Checks if a code snippet evals to a provided value after the compilation. This operation loads
   * all the generated classes and run the code snippet by reflection.
   *
   * @param value Expected result after running the code snippet.
   */
  fun Code.Source.evalsTo(
    value: Any?,
    onError: (Throwable) -> Any? = { throw it }
  ): Assert.SingleAssert = Assert.EvalsTo(this, value, onError)

  /** Returns a Source object from a String. */
  val String.source: Code.Source
    get() = Code.Source(text = this)

  /** Allows to provide several [Assert.SingleAssert]. */
  operator fun Assert.SingleAssert.plus(other: Assert.SingleAssert): Assert =
    Assert.Many(listOf(this, other))

  /** Allows to provide several [Assert.SingleAssert]. */
  fun allOf(vararg elements: Assert.SingleAssert): Assert =
    if (elements.isNotEmpty()) allOf(elements.asList()) else Assert.Many(emptyList())

  fun allOf(elements: List<Assert.SingleAssert>): Assert = Assert.Many(elements)
}

/** Represents the different types of [Assert] which will be managed. */
sealed class Assert {

  abstract class SingleAssert : Assert()
  internal data class Many(val asserts: List<SingleAssert>) : Assert()

  internal data class EvalsTo(
    val source: Code.Source,
    val output: Any?,
    val onError: (Throwable) -> Any?
  ) : SingleAssert()
  internal data class FailsWith(val f: (String) -> Boolean) : SingleAssert()
  internal data class CompilesWith(val f: (String) -> Boolean) : SingleAssert()
  internal sealed class CompilationResult : SingleAssert() {
    object Compiles : CompilationResult()
    object Fails : CompilationResult()
  }

  internal object Empty : SingleAssert()

  internal companion object : AssertSyntax {
    override val emptyAssert: SingleAssert = Empty
    override val compiles: SingleAssert = CompilationResult.Compiles
    override val fails: SingleAssert = CompilationResult.Fails
  }
}
