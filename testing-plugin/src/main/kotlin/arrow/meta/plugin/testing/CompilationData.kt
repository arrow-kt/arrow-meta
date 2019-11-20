package arrow.meta.plugin.testing

/**
 * Compilation data is a Monoid that can accumulate in its element as it's
 * composed and merged with other CompilationData elements
 */
internal data class CompilationData(
  val compilerPlugins: List<String> = emptyList(),
  val dependencies: List<String> = emptyList(),
  val sources: List<Code.Source> = emptyList()
) {

  operator fun plus(other: CompilationData): CompilationData =
    copy(
      compilerPlugins = compilerPlugins + other.compilerPlugins,
      dependencies = dependencies + other.dependencies,
      sources = sources + other.sources
    )

  companion object {
    val empty: CompilationData = CompilationData()
  }
}