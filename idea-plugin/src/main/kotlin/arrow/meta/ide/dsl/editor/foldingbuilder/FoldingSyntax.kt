package arrow.meta.ide.dsl.editor.foldingbuilder

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.folding.LanguageFolding
import com.intellij.openapi.editor.Document
import com.intellij.util.KeyedLazyInstance
import org.jetbrains.kotlin.idea.KotlinLanguage

interface FoldingSyntax {
  fun IdeMetaPlugin.languageFolding(
    builder: FoldingBuilder,
    lang: Language = KotlinLanguage.INSTANCE
  ): ExtensionPhase =
    extensionProvider(
      LanguageFolding.EP_NAME,
      object : KeyedLazyInstance<FoldingBuilder> {
        override fun getKey(): String = lang.displayName

        override fun getInstance(): FoldingBuilder = builder
      }
    )

  fun IdeMetaPlugin.addFoldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (node: ASTNode, document: Document) -> Array<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean,
    lang: Language = KotlinLanguage.INSTANCE
  ): ExtensionPhase =
    languageFolding(object : FoldingBuilder {
      override fun getPlaceholderText(node: ASTNode): String? =
        placeHolderText(node)

      override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> =
        foldRegions(node, document)

      override fun isCollapsedByDefault(node: ASTNode): Boolean =
        isCollapsedByDefault(node)
    },
      lang
    )
}