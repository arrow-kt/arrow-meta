@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.source.util.JavacTask
import com.sun.source.util.Plugin
import com.sun.tools.javac.api.BasicJavacTask
import com.sun.tools.javac.code.Lint
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.util.DiagnosticSource
import com.sun.tools.javac.util.JCDiagnostic
import com.sun.tools.javac.util.Log

public class AnalysisPlugin : Plugin {
  override fun getName(): String = NAME

  override fun init(task: JavacTask?, vararg args: String?) {
    val context: Context = (task as BasicJavacTask).context
    val logger = Log.instance(context)
    val diagnostics = JCDiagnostic.Factory.instance(context)
    logger.report(diagnostics.warning(
      Lint.LintCategory.RAW,
      DiagnosticSource.NO_SOURCE,
      JCDiagnostic.SimpleDiagnosticPosition(1),
      "Hello from $name"))
  }

  public companion object {
    public val NAME: String = "ArrowAnalysisJavaPlugin"
  }

}
