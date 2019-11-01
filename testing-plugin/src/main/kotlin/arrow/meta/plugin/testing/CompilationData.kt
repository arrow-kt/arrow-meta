package arrow.meta.plugin.testing

enum class CompilationStatus {
  OK,
  INTERNAL_ERROR,
  COMPILATION_ERROR,
  SCRIPT_EXECUTION_ERROR
}

/**
 * Compilation data is a Monoid that can accumulate in it's element as it's
 * composed and fushioned with other CompilationData elements
 */
data class CompilationData(
  val compilerPlugins: List<String> = emptyList(),
  val dependencies: List<String> = emptyList(),
  val source: List<String> = emptyList()
) {

  operator fun plus(other: CompilationData): CompilationData =
    copy(
      compilerPlugins = compilerPlugins + other.compilerPlugins,
      dependencies = dependencies + other.dependencies,
      source = source + other.source
    )

  companion object {
    val empty: CompilationData = CompilationData()
  }
}