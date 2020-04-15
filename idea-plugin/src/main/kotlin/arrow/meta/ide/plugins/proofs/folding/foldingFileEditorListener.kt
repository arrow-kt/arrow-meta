package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile

val IdeMetaPlugin.foldingFileEditor: ExtensionPhase
  get() = addFileEditorListener(
    fileOpened = { _: FileEditorManager, _: VirtualFile, _: FileEditor, document: Document ->
      EditorFactory.getInstance().getEditors(document).mapNotNull { editor ->
        editor.caretModel.addCaretListener(caretListener(
          caretPositionChanged = {
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
          })
        )
      }
    }
  )
