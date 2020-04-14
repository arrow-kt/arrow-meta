package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile

val IdeMetaPlugin.foldingFileEditor: ExtensionPhase
  get() = addFileEditorListener(
    fileOpened = { manager: FileEditorManager, file: VirtualFile ->
      val fileEditor = manager.getSelectedEditor(file)
      val document = FileDocumentManager.getInstance().getDocument(file)

      if (fileEditor == null || document == null) return@addFileEditorListener

      val editors: Array<Editor> = EditorFactory.getInstance().getEditors(document)
      if (editors.isNotEmpty()) {
        val editor = editors[0]
        editor.caretModel.addCaretListener(object : CaretListener {
          override fun caretPositionChanged(event: CaretEvent) {
            super.caretPositionChanged(event)
            val codeFoldingManager = CodeFoldingManager.getInstance(editor.project).apply {
              updateFoldRegions(editor)
            }

            editor.foldingModel.runBatchFoldingOperation {
              val line = editor.caretModel.logicalPosition.line
              editor.foldingModel.allFoldRegions
                .filter {
                  it.isExpanded &&
                    codeFoldingManager.isCollapsedByDefault(it) == true &&
                    it.document.getLineNumber(it.startOffset) != line
                }
                .map { it.isExpanded = false }
            }
          }
        })
      }
    }
  )