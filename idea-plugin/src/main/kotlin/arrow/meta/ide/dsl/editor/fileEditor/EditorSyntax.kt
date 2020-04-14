package arrow.meta.ide.dsl.editor.fileEditor

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.application.ApplicationProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile

interface EditorSyntax {

  /**
   * registers a [FileEditorManagerListener]
   */
  fun IdeMetaPlugin.addFileEditorListener(
    selectionChanged: (event: FileEditorManagerEvent) -> Unit = Noop.effect1,
    fileOpened: (manager: FileEditorManager, file: VirtualFile, fileEditor: FileEditor, document: Document) -> Unit,
    fileOpenedSync: (source: FileEditorManager, file: VirtualFile, editors: Pair<Array<FileEditor>, Array<FileEditorProvider>>) -> Unit = Noop.effect3,
    fileClosed: (source: FileEditorManager, file: VirtualFile) -> Unit = Noop.effect2
  ): ExtensionPhase =
    ApplicationProvider.FileEditorListener(fileEditorListener(selectionChanged, fileOpened, fileOpenedSync, fileClosed))

  fun EditorSyntax.fileEditorListener(
    selectionChanged: (event: FileEditorManagerEvent) -> Unit,
    fileOpened: (source: FileEditorManager, file: VirtualFile, fileEditor: FileEditor, document: Document) -> Unit,
    fileOpenedSync: (source: FileEditorManager, file: VirtualFile, editors: Pair<Array<FileEditor>, Array<FileEditorProvider>>) -> Unit,
    fileClosed: (source: FileEditorManager, file: VirtualFile) -> Unit
  ): FileEditorManagerListener =
    object : FileEditorManagerListener {
      override fun selectionChanged(event: FileEditorManagerEvent) =
        selectionChanged(event)

      override fun fileOpened(source: FileEditorManager, file: VirtualFile) =
        source.getSelectedEditor(file)?.let { fileEditor ->
          FileDocumentManager.getInstance().getDocument(file)?.let { document ->
            fileOpened(source, file, fileEditor, document)
          }
        } ?: Unit

      override fun fileOpenedSync(source: FileEditorManager, file: VirtualFile, editors: Pair<Array<FileEditor>, Array<FileEditorProvider>>) =
        fileOpenedSync(source, file, editors)

      override fun fileClosed(source: FileEditorManager, file: VirtualFile) =
        fileClosed(source, file)
    }

  fun EditorSyntax.addEditorCaretListener(
    caretAdded: (event: CaretEvent) -> Unit = Noop.effect1,
    caretPositionChanged: (event: CaretEvent) -> Unit,
    caretRemoved: (event: CaretEvent) -> Unit = Noop.effect1
  ): CaretListener =
    object : CaretListener {
      override fun caretAdded(event: CaretEvent) =
        caretAdded(event)

      override fun caretPositionChanged(event: CaretEvent) =
        caretPositionChanged(event)

      override fun caretRemoved(event: CaretEvent) =
        caretRemoved(event)
    }
}