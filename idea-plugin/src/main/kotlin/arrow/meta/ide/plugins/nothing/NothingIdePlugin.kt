package arrow.meta.ide.plugins.nothing

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.invoke
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.HtmlClassifierNamePolicy
import org.jetbrains.kotlin.idea.WrapValueParameterHandler
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.renderer.AnnotationArgumentsRenderingPolicy
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.sampleQuickDocs: Plugin
  get() = "QuickDocsDeclarations" {
    meta(
      addDocumentationProvider(
        quickNavigateInfo = { element, originalElement ->
          element.safeAs<KtDeclaration>()?.let { decl: KtDeclaration ->
            decl.analyze(BodyResolveMode.PARTIAL).let { context: BindingContext ->
              context[BindingContext.DECLARATION_TO_DESCRIPTOR, decl]?.let { descriptor: DeclarationDescriptor ->
                DeclarationRenderer.render(descriptor)
              }
            }
          }
        }
      )
    )
  }

private val DeclarationRenderer: DescriptorRenderer = DescriptorRenderer.HTML.withOptions {
  classifierNamePolicy = HtmlClassifierNamePolicy(ClassifierNamePolicy.SHORT)
  valueParametersHandler = WrapValueParameterHandler(valueParametersHandler)
  annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
  renderCompanionObjectName = true
  withDefinedIn = false
  eachAnnotationOnNewLine = true
  boldOnlyForNamesInHtml = true
}