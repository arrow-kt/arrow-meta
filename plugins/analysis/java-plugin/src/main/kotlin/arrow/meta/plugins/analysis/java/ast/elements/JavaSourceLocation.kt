@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import com.sun.source.tree.CompilationUnitTree

public class JavaSourceLocation(
  private val unit: CompilationUnitTree,
  private val startPos: Long,
  private val endPos: Long
) : CompilerMessageSourceLocation {
  override val column: Int
    get() = unit.lineMap.getColumnNumber(startPos).toInt()
  override val columnEnd: Int
    get() = unit.lineMap.getColumnNumber(endPos).toInt()
  override val line: Int
    get() = unit.lineMap.getLineNumber(startPos).toInt()
  override val lineEnd: Int
    get() = unit.lineMap.getLineNumber(endPos).toInt()
  override val lineContent: String?
    get() = null
  override val path: String
    get() = unit.sourceFile.name

  public companion object {
    public operator fun invoke(
      unit: CompilationUnitTree?,
      startPos: Long,
      endPos: Long
    ): JavaSourceLocation? = unit?.let { JavaSourceLocation(it, startPos, endPos) }
  }
}
