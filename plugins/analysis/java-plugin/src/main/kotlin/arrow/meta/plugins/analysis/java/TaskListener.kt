@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.source.util.JavacTask
import com.sun.source.util.TaskEvent
import com.sun.source.util.TaskListener

public abstract class PerKindTaskListener : TaskListener {
  override fun finished(e: TaskEvent?): Unit {
    when (e?.kind) {
      TaskEvent.Kind.PARSE -> afterParse(e)
      TaskEvent.Kind.ENTER -> afterEnter(e)
      TaskEvent.Kind.ANALYZE -> afterAnalyze(e)
      TaskEvent.Kind.GENERATE -> afterGenerate(e)
      else -> {}
    }
  }

  public fun afterParse(e: TaskEvent): Unit {}
  public fun afterEnter(e: TaskEvent): Unit {}
  public fun afterAnalyze(e: TaskEvent): Unit {}
  public fun afterGenerate(e: TaskEvent): Unit {}
}

public fun JavacTask.after(kind: TaskEvent.Kind, run: (TaskEvent, Resolver) -> Unit) {
  addTaskListener(
    object : TaskListener {
      override fun finished(e: TaskEvent?): Unit {
        if (e?.kind == kind) run(e, Resolver(this@after, e.compilationUnit))
      }
    }
  )
}
