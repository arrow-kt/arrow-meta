@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation

public class JavaSourceLocation(ctx: AnalysisContext, startPos: Long, endPos: Long) :
  CompilerMessageSourceLocation {
  override val column: Int = ctx.unit.lineMap.getColumnNumber(startPos).toInt()
  override val columnEnd: Int = ctx.unit.lineMap.getColumnNumber(endPos).toInt()
  override val line: Int = ctx.unit.lineMap.getLineNumber(startPos).toInt()
  override val lineEnd: Int = ctx.unit.lineMap.getLineNumber(endPos).toInt()
  override val lineContent: String? = null
  override val path: String = ctx.unit.sourceFile.name
}
