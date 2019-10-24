package arrow.meta.plugin.testing

data class Source(val text: String)
data class Dependency(val mavenCoordinates: String)
data class CompilerPlugin(
  val name: String,
  val dependencies: List<Dependency>
)

typealias CompilerTestInterpreter = (CompilerTest) -> Unit

data class CompilerTest(
  val config: CompilerTest.Companion.() -> List<Config> = { emptyList() },
  val code: CompilerTest.Companion.() -> Source, // TODO: Sources
  val assert: CompilerTest.Companion.() -> List<Assert> = { emptyList() }
) {
  fun run(interpret: CompilerTestInterpreter): Unit =
    interpret(this)

  companion object : Config.Syntax by Config, Assert.Syntax by Assert {
    operator fun invoke(f: Companion.() -> CompilerTest): CompilerTest =
      f(this)
  }
}

sealed class Config {
  data class AddCompilerPlugins(val plugins: List<CompilerPlugin>) : Config()
  data class AddDependencies(val dependencies: List<Dependency>) : Config()
  data class Many(val configs: List<Config>) : Config()
  object Empty : Config()
  interface Syntax {
    val emptyConfig: Config
    fun addCompilerPlugins(vararg element: CompilerPlugin): Config =
      Many(listOf(AddCompilerPlugins(element.toList())))

    fun addDependencies(vararg element: Dependency): Config =
      Many(listOf(AddDependencies(element.toList())))

    operator fun Config.plus(other: Config): List<Config> =
      listOf(this, other)

    fun List<Config>.toConfig(): Config =
      Many(this)
  }

  companion object : Syntax {
    override val emptyConfig: Config = Config.emptyConfig
  }
}

sealed class Assert {
  sealed class CompilationResult : Assert() {
    object Compiles : CompilationResult()
    object Fails : CompilationResult()
    data class FailsWith(val f: (String) -> Boolean) : Assert()
  }

  object Empty : Assert()
  data class QuoteOutputMatches(val source: Source) : Assert()
  data class EvalsTo(val source: Source, val output: Any?) : Assert()
  interface Syntax {
    val emptyAssert: Assert
    val compiles: Assert
    val fails: Assert
    fun failsWith(f: (String) -> Boolean): Assert = Assert.CompilationResult.FailsWith(f)
    fun quoteOutputMatches(source: Source): Assert = QuoteOutputMatches(source)
    infix fun Source.evalsTo(value: Any?): Assert = EvalsTo(this, value)
    val String.source: Source get() = Source(this)
  }

  companion object : Syntax {
    override val emptyAssert: Assert = Assert.Empty
    override val compiles: Assert = CompilationResult.Compiles
    override val fails: Assert = Assert.CompilationResult.Fails
  }
}