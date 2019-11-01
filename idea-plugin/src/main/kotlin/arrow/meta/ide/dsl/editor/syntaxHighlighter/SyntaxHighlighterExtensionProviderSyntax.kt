package arrow.meta.ide.dsl.editor.syntaxHighlighter

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.ideRegistry
import arrow.meta.phases.ExtensionPhase
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.lexer.KotlinLexer

interface SyntaxHighlighterExtensionProviderSyntax {
  // TODO: Test impl
  fun IdeMetaPlugin.addSyntaxHighlighter(
    highlightingLexer: Lexer = KotlinLexer(),
    tokenHighlights: (tokenType: IElementType?) -> Array<TextAttributesKey>
  ): ExtensionPhase =
    ideRegistry {
      SyntaxHighlighterFactory.LANGUAGE_FACTORY
        .addExplicitExtension(KotlinLanguage.INSTANCE, syntaxHighlighterFactory(
          this@SyntaxHighlighterExtensionProviderSyntax.syntaxHighlighter(highlightingLexer, tokenHighlights)
        ))
    }

  fun SyntaxHighlighterExtensionProviderSyntax.syntaxHighlighter(
    highlightingLexer: Lexer = KotlinLexer(),
    tokenHighlights: (tokenType: IElementType?) -> Array<TextAttributesKey>
  ): SyntaxHighlighter =
    object : SyntaxHighlighterBase() {
      override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        tokenHighlights(tokenType)

      override fun getHighlightingLexer(): Lexer =
        highlightingLexer
    }

  fun SyntaxHighlighterExtensionProviderSyntax.syntaxHighlighterFactory(
    syntaxHighlighter: SyntaxHighlighter
  ): SyntaxHighlighterFactory =
    object : SingleLazyInstanceSyntaxHighlighterFactory() {
      override fun createHighlighter(): SyntaxHighlighter =
        syntaxHighlighter
    }
}
