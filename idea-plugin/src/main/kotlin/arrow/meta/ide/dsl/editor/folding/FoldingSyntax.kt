package arrow.meta.ide.dsl.editor.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.folding.LanguageFolding
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.util.KeyedLazyInstance
import org.jetbrains.kotlin.idea.KotlinLanguage

interface FoldingSyntax {
  fun IdeMetaPlugin.addFoldingBuilder(
    placeHolderText: (node: ASTNode) -> String?,
    foldRegions: (root: PsiElement, document: Document, quick: Boolean) -> List<FoldingDescriptor>,
    isCollapsedByDefault: (node: ASTNode) -> Boolean,
    lang: Language = KotlinLanguage.INSTANCE
  ): ExtensionPhase =
    languageFolding(object : FoldingBuilderEx() {
      override fun getPlaceholderText(node: ASTNode): String? =
        placeHolderText(node)

      override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> =
        foldRegions(root, document, quick).toTypedArray()

      override fun isCollapsedByDefault(node: ASTNode): Boolean =
        isCollapsedByDefault(node)
    },
      lang
    )

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
}