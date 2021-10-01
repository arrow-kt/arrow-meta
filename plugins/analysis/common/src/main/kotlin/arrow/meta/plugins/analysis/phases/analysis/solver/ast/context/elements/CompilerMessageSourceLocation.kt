package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface CompilerMessageSourceLocation {
  val column: Int
  val columnEnd: Int
  val line: Int
  val lineContent: String?
  val lineEnd: Int
  val path: String
}
