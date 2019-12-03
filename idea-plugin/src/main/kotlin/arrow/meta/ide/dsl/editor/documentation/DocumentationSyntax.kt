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
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
   * import org.jetbrains.kotlin.idea.HtmlClassifierNamePolicy
   * import org.jetbrains.kotlin.idea.WrapValueParameterHandler
   * import org.jetbrains.kotlin.idea.caches.resolve.analyze
   * import org.jetbrains.kotlin.psi.KtDeclaration
   * import org.jetbrains.kotlin.renderer.AnnotationArgumentsRenderingPolicy
   * import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
   * import org.jetbrains.kotlin.renderer.DescriptorRenderer
   * import org.jetbrains.kotlin.resolve.BindingContext
   * import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * private val DeclarationRenderer: DescriptorRenderer = DescriptorRenderer.HTML.withOptions {
   *  classifierNamePolicy = HtmlClassifierNamePolicy(ClassifierNamePolicy.SHORT)
   *  valueParametersHandler = WrapValueParameterHandler(valueParametersHandler)
   *  annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
   *  renderCompanionObjectName = true
   *  withDefinedIn = false
   *  eachAnnotationOnNewLine = true
   *  boldOnlyForNamesInHtml = true
   * }
   *
   * //sampleStart
   * val IdeMetaPlugin.sampleQuickDocs: Plugin
   *  get() = "QuickDocsDeclarations" {
   *   meta(
   *    addDocumentationProvider(
   *     quickNavigateInfo = { element, originalElement ->
   *      element.safeAs<KtDeclaration>()?.let { decl: KtDeclaration ->
   *         decl.analyze(BodyResolveMode.PARTIAL).let { context: BindingContext ->
   *          context[BindingContext.DECLARATION_TO_DESCRIPTOR, decl]?.let { descriptor: DeclarationDescriptor ->
   *           DeclarationRenderer.render(descriptor) // this DeclarationRenderer pretty print's the descriptor
   *          }
   *         }
   *        }
   *       }
   *      )
   *     )
   *    }
   * //sampleEnd
   * ```
   * This example render's quickDocs on local declarations, but is insufficient for toplevel KtDeclarations.
   * In addition, this implementation should not render the complete Doc's only the minimal amount of data.
   * Hence, the KDoc's of this declaration should be added in [generateDoc].
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
   * @see [DocumentationProvider]
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