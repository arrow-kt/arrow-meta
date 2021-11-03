@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.source.util.JavacTask
import com.sun.source.util.Plugin
import com.sun.tools.javac.api.BasicJavacTask
import com.sun.tools.javac.code.Lint
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.util.DiagnosticSource
import com.sun.tools.javac.util.JCDiagnostic
import com.sun.tools.javac.util.JavacMessages
import com.sun.tools.javac.util.Log
import java.util.*

@Suppress("unused")
public class AnalysisMessages : ListResourceBundle() {
  override fun getContents(): Array<Array<Any>> =
    arrayOf(arrayOf("arrow-analysis.warn.hello", "Hello from {0}"))
}

public class AnalysisContext(context: Context) {
  public val logger: Log = Log.instance(context)
  private val messages: JavacMessages =
    JavacMessages.instance(context).apply { add(AnalysisMessages::class.qualifiedName) }
  public val diagnostics: JCDiagnostic.Factory = JCDiagnostic.Factory(messages, "arrow-analysis")

  public companion object {
    public operator fun invoke(task: BasicJavacTask): AnalysisContext =
      AnalysisContext(task.context)
  }
}

public class AnalysisPlugin : Plugin {
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
  }

  public companion object {
    public val NAME: String = "ArrowAnalysisJavaPlugin"
  }
}
