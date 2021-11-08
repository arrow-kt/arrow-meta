@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation

public class JavaSourceLocation(
  private val ctx: AnalysisContext,
  private val startPos: Long,
  private val endPos: Long
) : CompilerMessageSourceLocation {
  override val column: Int
    get() = ctx.unit.lineMap.getColumnNumber(startPos).toInt()
  override val columnEnd: Int
    get() = ctx.unit.lineMap.getColumnNumber(endPos).toInt()
  override val line: Int
    get() = ctx.unit.lineMap.getLineNumber(startPos).toInt()
  override val lineEnd: Int
    get() = ctx.unit.lineMap.getLineNumber(endPos).toInt()
  override val lineContent: String?
    get() = null
  override val path: String
    get() = ctx.unit.sourceFile.name
}
