package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager

val IdeMetaPlugin.typeFoldingHandler: ExtensionPhase
  get() = addBaseFoldingHandler(
    actionId = "CollapseTypeRegion",
    execute = { editor, caret, ctx ->
      println("Start CollapseTypeRegion")
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
  )
