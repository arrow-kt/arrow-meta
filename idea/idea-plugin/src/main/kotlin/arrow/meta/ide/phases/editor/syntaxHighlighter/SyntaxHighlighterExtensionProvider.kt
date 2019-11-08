package arrow.meta.ide.phases.editor.syntaxHighlighter

import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory

sealed class SyntaxHighlighterExtensionProvider : ExtensionPhase {
  data class RegisterSyntaxHighlighter(val factory: SyntaxHighlighterFactory) : SyntaxHighlighterExtensionProvider()
}