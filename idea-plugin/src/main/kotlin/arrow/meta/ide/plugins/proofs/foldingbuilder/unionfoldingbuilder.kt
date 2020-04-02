package arrow.meta.ide.plugins.proofs.foldingbuilder

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtVisitor


fun IdeMetaPlugin.addUnionFoldingBuilder(): ExtensionPhase =
  addFoldingBuilder(
    placeHolderText = { node: ASTNode ->
      "String | Int"
    },
    foldRegions = { node: ASTNode, document: Document ->
      val result = arrayListOf<FoldingDescriptor>()
      println("document=$document")
      println("node=$node")
      result.toTypedArray()
    },
    isCollapsedByDefault = { node: ASTNode ->
      true
    }
  )

class UnionFoldingBuilder : FoldingBuilderEx() {
  override fun getPlaceholderText(node: ASTNode): String? =
    "String | Int"

  override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
    val result = arrayListOf<FoldingDescriptor>()

    (root as KtElement).accept(object : KtVisitor<Boolean, Any?>() {
      override fun visitTypeReference(typeReference: KtTypeReference, data: Any?): Boolean {
        // TODO: check if type is Union and add it
        result.add(FoldingDescriptor(typeReference, typeReference.textRange))
        return super.visitTypeReference(typeReference, data)
      }
    }, null)

    return result.toTypedArray()
  }

  override fun isCollapsedByDefault(node: ASTNode): Boolean =
    true

  private fun PsiElement.getFoldingDescriptor(): FoldingDescriptor? =
    FoldingDescriptor(this, textRange)
}