package arrow.meta.plugin.testing

import arrow.meta.Plugin

data class Source(val text: String)
data class Dependency(val mavenCoordinates: String)
data class CompilerPlugin(
  val name: String,
  val dependencies: List<Dependency>
)

typealias CompilerTestInterpreter = (CompilerTest) -> Unit

data class CompilerTest(
  val config: Companion.() -> List<Config> = { emptyList() },
  val code: Companion.() -> Source, // TODO: Sources
  val assert: Companion.() -> List<Assert> = { emptyList() }
) {
  fun run(interpret: CompilerTestInterpreter): Unit =
    interpret(this)

  companion object : ConfigSyntax by Config, AssertSyntax by Assert {
    operator fun invoke(f: Companion.() -> CompilerTest): CompilerTest =
      f(this)
  }
}

interface ConfigSyntax {
  val emptyConfig: Config
  fun addCompilerPlugins(vararg element: CompilerPlugin): Config =
    Config.Many(listOf(Config.AddCompilerPlugins(element.toList())))

  fun addMetaPlugin(vararg element: Plugin): Config =
    Config.Many(listOf(Config.AddMetaPlugins(element.toList())))

  fun addDependencies(vararg element: Dependency): Config =
    Config.Many(listOf(Config.AddDependencies(element.toList())))

  operator fun Config.plus(other: Config): List<Config> =
    listOf(this, other)

  fun List<Config>.toConfig(): Config =
    Config.Many(this)

  val metaDependencies: List<Config>
    get() {
      val currentVersion = System.getProperty("CURRENT_VERSION")
      val arrowVersion = System.getProperty("ARROW_VERSION")
      val compilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin:$currentVersion:all")))
      val arrowAnnotations = Dependency("arrow-annotations:$arrowVersion")
      return CompilerTest.addCompilerPlugins(compilerPlugin) + CompilerTest.addDependencies(arrowAnnotations)
    }
}

sealed class Config {
  data class AddCompilerPlugins(val plugins: List<CompilerPlugin>) : Config()
  data class AddMetaPlugins(val plugins: List<Plugin>) : Config()
  data class AddDependencies(val dependencies: List<Dependency>) : Config()
  data class Many(val configs: List<Config>) : Config()
  object Empty : Config()

  companion object : ConfigSyntax {
    override val emptyConfig: Config = Config.emptyConfig
  }
}

interface AssertSyntax {
  val emptyAssert: Assert
  val compiles: Assert
  val fails: Assert
  fun failsWith(f: (String) -> Boolean): Assert = Assert.FailsWith(f)
  fun quoteOutputMatches(source: Source): Assert = Assert.QuoteOutputMatches(source)
  infix fun Source.evalsTo(value: Any?): Assert = Assert.EvalsTo(this, value)
  val String.source: Source get() = Source(this)

  operator fun Assert.plus(other: Assert): List<Assert> =
    listOf(this, other)

  fun allOf(vararg elements: Assert): List<Assert> =
    if (elements.isNotEmpty()) elements.asList() else emptyList()
}

sealed class Assert {
  sealed class CompilationResult : Assert() {
    object Compiles : CompilationResult()
    object Fails : CompilationResult()
  }

  object Empty : Assert()
  data class QuoteOutputMatches(val source: Source) : Assert()
  data class EvalsTo(val source: Source, val output: Any?) : Assert()
  data class FailsWith(val f: (String) -> Boolean) : Assert()

  companion object : AssertSyntax {
    override val emptyAssert: Assert = Assert.Empty
    override val compiles: Assert = CompilationResult.Compiles
    override val fails: Assert = Assert.CompilationResult.Fails
  }
}
