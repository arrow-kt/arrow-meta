package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation

class KotlinCompilerMessageSourceLocation(val impl: org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation) : CompilerMessageSourceLocation {
  override val column: Int
    get() = impl.column
  override val columnEnd: Int
    get() = impl.columnEnd
  override val line: Int
    get() = impl.line
  override val lineContent: String?
    get() = impl.lineContent
  override val lineEnd: Int
    get() = impl.lineEnd
  override val path: String
    get() = impl.path
}
