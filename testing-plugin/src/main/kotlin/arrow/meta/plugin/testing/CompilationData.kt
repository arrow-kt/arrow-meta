package arrow.meta.plugin.testing

enum class CompilationStatus {
  OK,
  INTERNAL_ERROR,
  COMPILATION_ERROR,
  SCRIPT_EXECUTION_ERROR
}

data class CompilationData(
  val compilerPlugins: List<String>,
  val dependencies: List<String> = emptyList(),
  val source: String
)