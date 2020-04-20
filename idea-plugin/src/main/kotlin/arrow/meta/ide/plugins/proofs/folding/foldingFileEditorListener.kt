package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.codeInsight.folding.impl.EditorFoldingInfo
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.event.CaretEvent
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.foldingCaretListener: ExtensionPhase
  get() = addCaretListener(
    caretPositionChanged = { _: Document, _: CaretEvent ->
      val codeFoldingManager = CodeFoldingManager.getInstance(project).apply {
        updateFoldRegions(this@addCaretListener)
      }

      foldingModel.runBatchFoldingOperation {
        val line = caretModel.logicalPosition.line
        val editorFoldingInfo = EditorFoldingInfo.get(this)
        foldingModel.allFoldRegions
          .filter { foldRegion: FoldRegion ->
            foldingRegionMatchingTypes(editorFoldingInfo, foldRegion)
          }
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

private fun foldingRegionMatchingTypes(editorFoldingInfo: EditorFoldingInfo, foldRegion: FoldRegion): Boolean {
  val psiElement = editorFoldingInfo.getPsiElement(foldRegion)
  return psiElement.safeAs<KtTypeReference>()?.let {
    unionTypeMatches(it) || tupleTypeMatches(it) || kindTypeMatches(it)
  } ?: false
}
