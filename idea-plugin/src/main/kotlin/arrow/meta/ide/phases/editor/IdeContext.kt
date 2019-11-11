package arrow.meta.ide.phases.editor

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer

object IdeContext {
  val dispose: Disposable = Disposer.newDisposable()
}