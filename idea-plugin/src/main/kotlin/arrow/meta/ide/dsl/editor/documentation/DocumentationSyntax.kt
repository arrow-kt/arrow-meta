package arrow.meta.ide.dsl.editor.documentation

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

/**
 * [DocumentationSyntax] registers an extension for documentation support.
 * @see [Documentation](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/documentation.html?search=doc)
 */
interface DocumentationSyntax {
  /**
   * registers a [DocumentationProvider].
   * Users are now able to hover over descriptors and see the provided documentation.
   * @see quickDocumentationProvider
   */
  fun IdeMetaPlugin.addDocumentationProvider(
    quickNavigateInfo: (element: PsiElement, originalElement: PsiElement) -> String? = Noop.nullable2(),
    generateDoc: (element: PsiElement, originalElement: PsiElement) -> String? = Noop.nullable2(),
    documentationElementForLink: (psiManager: PsiManager, link: String, context: PsiElement) -> PsiElement? = Noop.nullable3()
  ): ExtensionPhase =
    ExtensionProvider.AddExtension(
      DocumentationProvider.EP_NAME,
      quickDocumentationProvider(quickNavigateInfo, generateDoc, documentationElementForLink),
      LoadingOrder.FIRST)

  /**
   * An example is here {@link https://github.com/JetBrains/kotlin/blob/49d6bbbd6b1c2fea85df03af047aa0cf21ce0b97/idea/src/org/jetbrains/kotlin/idea/KotlinQuickDocumentationProvider.kt#L148}
   * @param quickNavigateInfo returns the short version of the Doc
   * @param generateDoc returns the entire Doc
   * @param documentationElementForLink given a [link] and a [context] this function resolves the PsiElement of that link
   */
  fun DocumentationSyntax.quickDocumentationProvider(
    quickNavigateInfo: (element: PsiElement, originalElement: PsiElement) -> String?,
    generateDoc: (element: PsiElement, originalElement: PsiElement) -> String?,
    documentationElementForLink: (psiManager: PsiManager, link: String, context: PsiElement) -> PsiElement?
  ): AbstractDocumentationProvider =
    object : AbstractDocumentationProvider() {
      override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? =
        element?.let { el -> originalElement?.let { original -> quickNavigateInfo(el, original) } }

      override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? =
        element?.let { el -> originalElement?.let { original -> generateDoc(el, original) } }

      override fun getDocumentationElementForLink(psiManager: PsiManager?, link: String?, context: PsiElement?): PsiElement? =
        psiManager?.let { manager -> link?.let { l -> context?.let { ctx -> documentationElementForLink(manager, l, ctx) } } }
    }
}