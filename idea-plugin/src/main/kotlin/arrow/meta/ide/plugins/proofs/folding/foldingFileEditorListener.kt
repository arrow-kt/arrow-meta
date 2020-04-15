package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.editor.event.CaretEvent

val IdeMetaPlugin.foldingCaretListener: ExtensionPhase
  get() = addCaretListener(
    caretPositionChanged = { _: CaretEvent ->
      val codeFoldingManager = CodeFoldingManager.getInstance(project).apply {
        updateFoldRegions(this@addCaretListener)
      }

      foldingModel.runBatchFoldingOperation {
        val line = caretModel.logicalPosition.line
        foldingModel.allFoldRegions
          .filter {
            it.isExpanded &&
              codeFoldingManager.isCollapsedByDefault(it) == true &&
              it.document.getLineNumber(it.startOffset) != line
          }
          .map { it.isExpanded = false }
      }
    }
  )
