@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

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
    val ctx = AnalysisContext(task as BasicJavacTask)
    ctx.logger.report(
      ctx.diagnostics.warning(
        Lint.LintCategory.RAW,
        DiagnosticSource.NO_SOURCE,
        JCDiagnostic.SimpleDiagnosticPosition(1),
        "hello",
        name
      )
    )
    task.after(TaskEvent.Kind.ENTER) { e, resolver ->
      resolver.run {
        val tys = e.compilationUnit.typeDecls.map { it.resolve() }
      }
    }
  }

  public companion object {
    public val NAME: String = "ArrowAnalysisJavaPlugin"
  }
}
