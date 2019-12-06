package arrow.meta.ide.phases.editor.syntaxHighlighter

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import arrow.meta.ide.dsl.editor.syntaxHighlighter.SyntaxHighlighterSyntax
import com.intellij.lang.Language

/**
 * @see [SyntaxHighlighterSyntax]
 */
sealed class SyntaxHighlighterExtensionProvider : ExtensionPhase {
  /**
   * @see [SyntaxHighlighterSyntax.addSyntaxHighlighter]
   */
  data class RegisterSyntaxHighlighter(val factory: SyntaxHighlighterFactory, val language: Language) : SyntaxHighlighterExtensionProvider()
}