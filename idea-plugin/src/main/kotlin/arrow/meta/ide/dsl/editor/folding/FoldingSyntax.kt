package arrow.meta.ide.dsl.editor.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.idea.KotlinLanguage

interface FoldingSyntax {

  fun IdeMetaPlugin.registerFoldingBuilder(foldingBuilder: FoldingBuilder, lang: Language = KotlinLanguage.INSTANCE): ExtensionPhase =
    ExtensionProvider.AddFoldingBuilder(lang, foldingBuilder)

  fun FoldingSyntax.foldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (node: ASTNode, document: Document) -> Array<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean,
    lang: Language = KotlinLanguage.INSTANCE
  ): FoldingBuilder =
    object : FoldingBuilder {
      override fun getPlaceholderText(node: ASTNode): String? =
        placeHolderText(node)

      override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> =
        foldRegions(node, document)

      override fun isCollapsedByDefault(node: ASTNode): Boolean =
        isCollapsedByDefault(node)
    }
}