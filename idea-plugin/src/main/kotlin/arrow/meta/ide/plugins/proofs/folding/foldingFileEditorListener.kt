package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.CaretEvent

val IdeMetaPlugin.foldingCaretListener: ExtensionPhase
  get() = addCaretListener(
    caretPositionChanged = { _: Document, _: CaretEvent ->
      val codeFoldingManager = CodeFoldingManager.getInstance(project).apply {
        updateFoldRegions(this@addCaretListener)
      }

      foldingModel.runBatchFoldingOperation {
        val line = caretModel.logicalPosition.line
        foldingModel.allFoldRegions
          .filter {
            codeFoldingManager.isCollapsedByDefault(it)?.let { collapsedByDefault ->
              it.isExpanded &&
                collapsedByDefault &&
                it.document.getLineNumber(it.startOffset) != line
            } ?: false
          }
          .map { it.isExpanded = false }
      }
    }
  )
