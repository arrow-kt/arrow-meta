package arrow.meta.ide.dsl.editor.parser

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

interface ParserSyntax {
  fun IdeMetaPlugin.addParser(
    parser: (Project) -> PsiParser,
    file: (FileViewProvider) -> PsiFile,
    stringLiteralElements: TokenSet,
    fileNodeType: IFileElementType,
    lexer: (Project) -> Lexer,
    element: (ASTNode) -> PsiElement,
    commentTokens: TokenSet
  ): ExtensionPhase =
    parser(parser(parser, file, stringLiteralElements, fileNodeType, lexer, element, commentTokens))

  fun IdeMetaPlugin.parser(parser: ParserDefinition): ExtensionPhase =
    extensionProvider(LanguageParserDefinitions.INSTANCE, parser)

  fun ParserSyntax.parser(
    parser: (Project) -> PsiParser,
    file: (FileViewProvider) -> PsiFile,
    stringLiteralElements: TokenSet,
    fileNodeType: IFileElementType,
    lexer: (Project) -> Lexer,
    element: (ASTNode) -> PsiElement,
    commentTokens: TokenSet
  ): ParserDefinition =
    object : ParserDefinition {
      override fun createParser(project: Project): PsiParser = parser(project)

      override fun createFile(viewProvider: FileViewProvider): PsiFile = file(viewProvider)

      override fun getStringLiteralElements(): TokenSet = stringLiteralElements

      override fun getFileNodeType(): IFileElementType = fileNodeType

      override fun createLexer(project: Project): Lexer = lexer(project)

      override fun createElement(node: ASTNode): PsiElement = element(node)

      override fun getCommentTokens(): TokenSet = commentTokens
    }
}