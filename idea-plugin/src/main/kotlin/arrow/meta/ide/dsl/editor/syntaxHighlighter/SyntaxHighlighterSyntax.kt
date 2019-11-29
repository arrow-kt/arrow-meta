package arrow.meta.ide.dsl.editor.syntaxHighlighter

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.syntaxHighlighter.SyntaxHighlighterExtensionProvider
import arrow.meta.phases.ExtensionPhase
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KotlinLexer

interface SyntaxHighlighterSyntax {
  // TODO: Test impl
  fun IdeMetaPlugin.addSyntaxHighlighter(
    highlightingLexer: Lexer = KotlinLexer(),
    tokenHighlights: (tokenType: IElementType?) -> Array<TextAttributesKey>
  ): ExtensionPhase =
    SyntaxHighlighterExtensionProvider.RegisterSyntaxHighlighter(
      syntaxHighlighterFactory(
        this@SyntaxHighlighterSyntax.syntaxHighlighter(highlightingLexer, tokenHighlights)
      )
    )

  fun SyntaxHighlighterSyntax.syntaxHighlighter(
    highlightingLexer: Lexer = KotlinLexer(),
    tokenHighlights: (tokenType: IElementType?) -> Array<TextAttributesKey>
  ): SyntaxHighlighter =
    object : SyntaxHighlighterBase() {
      override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        tokenHighlights(tokenType)

      override fun getHighlightingLexer(): Lexer =
        highlightingLexer
    }

  fun SyntaxHighlighterSyntax.syntaxHighlighterFactory(
    syntaxHighlighter: SyntaxHighlighter
  ): SyntaxHighlighterFactory =
    object : SingleLazyInstanceSyntaxHighlighterFactory() {
      override fun createHighlighter(): SyntaxHighlighter =
        syntaxHighlighter
    }
}
