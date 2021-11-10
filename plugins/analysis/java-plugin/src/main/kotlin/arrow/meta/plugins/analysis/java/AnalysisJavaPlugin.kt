@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import arrow.meta.plugins.analysis.java.ast.JavaResolutionContext
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaFunctionDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaElement
import arrow.meta.plugins.analysis.java.ast.elements.JavaMethod
import arrow.meta.plugins.analysis.java.ast.elements.OurTreeVisitor
import arrow.meta.plugins.analysis.java.ast.elements.visitRecursively
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromDSL
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import com.sun.source.tree.MethodTree
import com.sun.source.util.JavacTask
import com.sun.source.util.Plugin
import com.sun.source.util.TaskEvent
import com.sun.tools.javac.api.BasicJavacTask
import com.sun.tools.javac.code.Lint
import com.sun.tools.javac.util.DiagnosticSource
import com.sun.tools.javac.util.JCDiagnostic
import java.util.*

public class AnalysisJavaPlugin : Plugin {
  override fun getName(): String = NAME

  override fun init(task: JavacTask?, vararg args: String?) {
    val javaTask = task as BasicJavacTask
    val ctx = AnalysisContextWithoutResolver(task)
    ctx.logger.report(
      ctx.diagnostics.warning(
        Lint.LintCategory.RAW,
        DiagnosticSource.NO_SOURCE,
        JCDiagnostic.SimpleDiagnosticPosition(1),
        "hello",
        name
      )
    )
    val solverState = SolverState(NameProvider())
    task.after(TaskEvent.Kind.ENTER) { e, unit ->
      val ctx = AnalysisContext(task, unit)
      unit.visitRecursively(
        object : OurTreeVisitor<Unit>(Unit) {
          override fun visitMethod(node: MethodTree, p: Unit?) {
            val decl: JavaMethod = node.model(ctx)
            val descr: JavaFunctionDescriptor = ctx.resolver.resolve(node).model(ctx)
            decl.collectConstraintsFromDSL(solverState, JavaResolutionContext(ctx), descr)
          }
        }
      )
    }
    task.before(TaskEvent.Kind.ANALYZE) { e, unit -> }

    task.after(TaskEvent.Kind.ANALYZE) { e, unit ->
      AnalysisContext(task, unit).run {
        val tys: List<JavaElement> = e.compilationUnit.typeDecls.map { it.model(this) }
      }
    }
  }

  public companion object {
    public val NAME: String = "ArrowAnalysisJavaPlugin"
  }
}
