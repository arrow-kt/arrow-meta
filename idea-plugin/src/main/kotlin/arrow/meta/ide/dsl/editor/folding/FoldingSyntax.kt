package arrow.meta.ide.dsl.editor.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.typeReferences
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

interface FoldingSyntax {

  /**
   * Utility to add a [FoldingBuilder]
   * @param match: matches on the specified KtTypeReference and adds the element to the folding region
   * @param hint: the resulting hint for the matched folding region
   */
  fun IdeMetaPlugin.addFoldingBuilder(
    match: (KtTypeReference) -> Boolean,
    hint: (KtTypeReference) -> String
  ): ExtensionPhase =
    addFoldingBuilder(
      placeHolderText = { node: ASTNode ->
        node.psi.safeAs<KtTypeReference>()?.let {
          if (match(it)) {
            hint(it)
          } else ""
        }
      },
      foldRegions = { element: PsiElement, _: Document, _: Boolean ->
        (element as KtElement).typeReferences
          .filter { match(it) }
          .map { FoldingDescriptor(it, it.textRange) }
      },
      isCollapsedByDefault = { node: ASTNode ->
        node.psi.safeAs<KtTypeReference>()?.let(match) ?: false
      })

  fun IdeMetaPlugin.addFoldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (node: ASTNode, document: Document) -> List<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean,
    lang: Language = KotlinLanguage.INSTANCE): ExtensionPhase =
    registerFoldingBuilder(foldingBuilder(placeHolderText, foldRegions, isCollapsedByDefault), lang)

  fun IdeMetaPlugin.addFoldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (element: PsiElement, document: Document, quick: Boolean) -> List<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean,
    lang: Language = KotlinLanguage.INSTANCE): ExtensionPhase =
    registerFoldingBuilder(foldingBuilder(placeHolderText, foldRegions, isCollapsedByDefault), lang)

  fun IdeMetaPlugin.registerFoldingBuilder(foldingBuilder: FoldingBuilder, lang: Language = KotlinLanguage.INSTANCE): ExtensionPhase =
    ExtensionProvider.AddFoldingBuilder(lang, foldingBuilder)

  fun FoldingSyntax.foldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (node: ASTNode, document: Document) -> List<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean
  ): FoldingBuilder =
    object : FoldingBuilder {
      override fun getPlaceholderText(node: ASTNode): String? =
        placeHolderText(node)

      override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> =
        foldRegions(node, document).toTypedArray()

      override fun isCollapsedByDefault(node: ASTNode): Boolean =
        isCollapsedByDefault(node)
    }

  fun FoldingSyntax.foldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (element: PsiElement, document: Document, quick: Boolean) -> List<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean
  ): FoldingBuilderEx =
    object : FoldingBuilderEx() {
      override fun getPlaceholderText(node: ASTNode): String? =
        placeHolderText(node)

      override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> =
        foldRegions(root, document, quick).toTypedArray()

      override fun isCollapsedByDefault(node: ASTNode): Boolean =
        isCollapsedByDefault(node)
    }
}