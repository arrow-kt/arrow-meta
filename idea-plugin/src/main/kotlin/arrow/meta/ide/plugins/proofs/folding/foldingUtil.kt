package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference

fun IdeMetaPlugin.addFoldingBuilder(
  isTypeMatching: (KtTypeReference) -> Boolean,
  toFoldString: (KtTypeReference) -> String): ExtensionPhase =
  addFoldingBuilder(
    placeHolderText = { node: ASTNode ->
      (node.psi as? KtTypeReference)?.let {
        if (isTypeMatching(it)) {
          toFoldString(it)
        } else ""
      }
    },
    foldRegions = { element: PsiElement, document: Document, _: Boolean ->
      val editors: Array<Editor> = EditorFactory.getInstance().getEditors(document)
      if (editors.isNotEmpty()) {
        val editor = editors[0]
        editor.caretModel.addCaretListener(object : CaretListener {
          override fun caretPositionChanged(event: CaretEvent) {
            super.caretPositionChanged(event)
            println("CollapseTypeRegion caretPositionChanged: $event")
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
      (element as KtElement).typeReferences
        .filter { isTypeMatching(it) }
        .map { FoldingDescriptor(it, it.textRange) }
    },
    isCollapsedByDefault = {
      true
    })
