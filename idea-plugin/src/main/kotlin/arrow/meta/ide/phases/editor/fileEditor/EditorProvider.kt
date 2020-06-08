package arrow.meta.ide.phases.editor.fileEditor

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.fileEditor.FileEditorManagerListener

sealed class EditorProvider : ExtensionPhase {
  /**
   * @see EditorSyntax
   */
  data class FileEditorListener(val listener: FileEditorManagerListener) : EditorProvider()
}