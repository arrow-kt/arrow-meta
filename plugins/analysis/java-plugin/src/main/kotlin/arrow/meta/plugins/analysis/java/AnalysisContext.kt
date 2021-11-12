@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.source.tree.CompilationUnitTree
import com.sun.tools.javac.api.BasicJavacTask
import com.sun.tools.javac.code.Symtab
import com.sun.tools.javac.comp.Modules
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeCopier
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.util.JCDiagnostic
import com.sun.tools.javac.util.JavacMessages
import com.sun.tools.javac.util.Log
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

public open class AnalysisContextWithoutResolver(
  public val context: Context,
  public val types: Types,
  public val elements: Elements
) {
  public val logger: Log = Log.instance(context)
  private val messages: JavacMessages =
    JavacMessages.instance(context).apply { add(AnalysisMessages::class.qualifiedName) }
  public val diagnostics: JCDiagnostic.Factory = JCDiagnostic.Factory(messages, "arrow-analysis")
  public val symbolTable: Symtab = Symtab.instance(context)
  public val modules: Modules = Modules.instance(context)

  public fun copy(t: JCTree): JCTree {
    val maker = TreeMaker.instance(context)
    val copier = TreeCopier<Unit>(maker)
    return copier.copy(t)
  }

  public companion object {
    public operator fun invoke(task: BasicJavacTask): AnalysisContextWithoutResolver =
      AnalysisContextWithoutResolver(task.context, task.types, task.elements)
  }
}

public class AnalysisContext(
  context: Context,
  types: Types,
  elements: Elements,
  public val unit: CompilationUnitTree,
  public val resolver: Resolver
) : AnalysisContextWithoutResolver(context, types, elements) {
  public companion object {
    public operator fun invoke(task: BasicJavacTask, unit: CompilationUnitTree): AnalysisContext =
      AnalysisContext(task.context, task.types, task.elements, unit, Resolver(task, unit))
  }
}
