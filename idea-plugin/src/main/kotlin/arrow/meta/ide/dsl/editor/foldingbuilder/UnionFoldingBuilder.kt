package arrow.meta.ide.dsl.editor.foldingbuilder

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement

class UnionFoldingBuilder : FoldingBuilderEx() {
  override fun getPlaceholderText(node: ASTNode): String? =
    "String | Int"

  override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
    val result = arrayListOf<FoldingDescriptor>()
    println("document=$document")
    println("node=$root")
    return result.toTypedArray()
  }

  override fun isCollapsedByDefault(node: ASTNode): Boolean =
    true
}
