package arrow.meta.ide.dsl.editor.syntaxHighlighter

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.syntaxHighlighter.SyntaxHighlighterExtensionProvider
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.Language
import arrow.meta.ide.dsl.editor.color.ColorSyntax
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.PsiParser

/**
 * [SyntaxHighlighterExtensionProvider] provides means to register [SyntaxHighlighter]s for Languages.
 * SyntaxHighlighters solely specify highlighted tokens, which are generated from the Lexer.
 * [ColorSyntax] allows to highlight additional descriptors from the `Parser` or [Annotator]. You may create a `Parser` with [PsiParser] an example is here [org.jetbrains.kotlin.parsing.KotlinParser].
 * Check out the Docs on [Syntax Highlighting](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html) or
 * [How to create a Syntax Highlighter](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/syntax_highlighter_and_color_settings_page.html).
 */
interface SyntaxHighlighterSyntax {
  // TODO: Write Tests
  // TODO: Add helper extension's to create Lexer, Annotator

  /**
   * @param tokenHighlights specifies the generated tokens from the lexer that need to be highlighted
   * The following example registers a sample amount of the generated Tokens from the KotlinLexer.
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
   * import com.intellij.openapi.editor.colors.TextAttributesKey
   * import com.intellij.psi.tree.IElementType
   * import org.jetbrains.kotlin.idea.KotlinLanguage
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingLexer
   * import org.jetbrains.kotlin.lexer.KtTokens
   *
   * //sampleStart
   * val IdeMetaPlugin.expressionHints: Plugin
   *  get() = "MiniKotlinHighlighter" {
   *   meta(
   *    addSyntaxHighlighter(
   *     highlightingLexer = KotlinHighlightingLexer(),
   *     language = KotlinLanguage.INSTANCE,
   *     tokenHighlights = { token ->
   *      tokenToHighlighting[token]?.let { arrayOf(it) } ?: emptyArray()
   *     }
   *    )
   *   )
   *  }
   * //sampleEnd
   * val tokenToHighlighting: Map<in IElementType, TextAttributesKey>
   *   get() = mapOf(
   *     KtTokens.KEYWORDS to DefaultLanguageHighlighterColors.KEYWORD,
   *     KtTokens.`AS_SAFE` to DefaultLanguageHighlighterColors.KEYWORD,
   *     KtTokens.INTEGER_LITERAL to DefaultLanguageHighlighterColors.NUMBER,
   *     KtTokens.FLOAT_LITERAL to DefaultLanguageHighlighterColors.NUMBER,
   *     KtTokens.DOC_COMMENT to DefaultLanguageHighlighterColors.DOC_COMMENT,
   *     KtTokens.ARROW to DefaultLanguageHighlighterColors.PARENTHESES,
   *     KtTokens.LPAR to DefaultLanguageHighlighterColors.PARENTHESES,
   *     KtTokens.RPAR to DefaultLanguageHighlighterColors.PARENTHESES
   *     //... and more tokens
   *   )
   * ```
   * In fact, the latter is a minimal example from the existing [KotlinHighlighter].
   * @see SyntaxHighlighterSyntax
   * @see syntaxHighlighterFactory
   * @see KotlinHighlighter
   */
  fun IdeMetaPlugin.addSyntaxHighlighter(
    highlightingLexer: Lexer,
    language: Language,
    tokenHighlights: (tokenType: IElementType) -> Array<TextAttributesKey>
  ): ExtensionPhase =
    SyntaxHighlighterExtensionProvider.RegisterSyntaxHighlighter(
      syntaxHighlighterFactory(this@SyntaxHighlighterSyntax.syntaxHighlighter(highlightingLexer, tokenHighlights)),
      language
    )

  fun SyntaxHighlighterSyntax.syntaxHighlighter(
    highlightingLexer: Lexer,
    tokenHighlights: (tokenType: IElementType) -> Array<TextAttributesKey>
  ): SyntaxHighlighter =
    object : SyntaxHighlighterBase() {
      override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        tokenType?.let(tokenHighlights) ?: emptyArray()

      override fun getHighlightingLexer(): Lexer =
        highlightingLexer
    }

  /**
   * [SingleLazyInstanceSyntaxHighlighterFactory] is a wrapper over the underlying [SyntaxHighlighter]
   */
  fun SyntaxHighlighterSyntax.syntaxHighlighterFactory(
    syntaxHighlighter: SyntaxHighlighter
  ): SyntaxHighlighterFactory =
    object : SingleLazyInstanceSyntaxHighlighterFactory() {
      override fun createHighlighter(): SyntaxHighlighter =
        syntaxHighlighter
    }
}
