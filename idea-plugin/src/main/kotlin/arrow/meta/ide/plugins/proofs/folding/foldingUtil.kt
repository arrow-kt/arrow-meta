package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
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
    foldRegions = { element: PsiElement, _: Document, _: Boolean ->
      (element as KtElement).typeReferences
        .filter { isTypeMatching(it) }
        .map { FoldingDescriptor(it, it.textRange) }
    },
    isCollapsedByDefault = {
      true
    })
