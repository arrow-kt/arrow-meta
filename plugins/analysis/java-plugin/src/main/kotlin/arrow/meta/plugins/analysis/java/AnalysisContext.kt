@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.tools.javac.api.BasicJavacTask
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.util.JCDiagnostic
import com.sun.tools.javac.util.JavacMessages
import com.sun.tools.javac.util.Log

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
