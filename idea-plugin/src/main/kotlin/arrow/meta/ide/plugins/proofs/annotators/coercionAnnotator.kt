package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.isCoerced
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diff.impl.util.GutterActionRenderer
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.p
import org.jetbrains.kotlin.idea.HtmlClassifierNamePolicy
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.idea.WrapValueParameterHandler
import org.jetbrains.kotlin.idea.decompiler.navigation.SourceNavigationHelper
import org.jetbrains.kotlin.idea.kdoc.KDocRenderer
import org.jetbrains.kotlin.idea.kdoc.findKDoc
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.renderer.AnnotationArgumentsRenderingPolicy
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer.Companion.HTML
import org.jetbrains.kotlin.renderer.PropertyAccessorRenderingPolicy
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.awt.Color
import java.awt.Font

val IdeMetaPlugin.coercionAnnotator: ExtensionPhase
  get() = Composite(
    coercionKtPropertyAnnotator,
    coercionKtValArgAnnotator
  )

private val coercionAnnotatorTextAttributes =
  TextAttributes(null, null, Color(192, 192, 192), EffectType.WAVE_UNDERSCORE, Font.PLAIN)

val IdeMetaPlugin.coercionKtPropertyAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtProperty>()
        ?.takeIf { ctx.isCoerced(it) }
        ?.let { psiElement: KtProperty ->
          val message = psiElement.participatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
                html {
                  body {
                    p {
                      "Implicit coercion applied by ${KDocRenderer.renderKDocContent(kDocTag)}"
                    } + p {
                      KotlinQuickDocumentationProvider().getQuickNavigateInfo(
                        proof.through.findPsi(),
                        SourceNavigationHelper.getOriginalElement((proof.through.findPsi() as KtDeclaration))
                      ).orEmpty()
                    }
                  }
                }.render()
              }
              // SourceNavigationHelper.getNavigationElement(proof.through.containingDeclaration)
              // (proof.through.source as KotlinSourceElement).psi.references
//              "Implicit coercion applied by \n" +
//                IdeDescriptorRenderers.SOURCE_CODE.render(proof.through)
            }
          } ?: "Proof not found"
          psiElement.delegateExpressionOrInitializer?.let {
            val annotation = holder.createAnnotation(HighlightSeverity.INFORMATION, it.textRange, null, message)
            annotation.enforcedTextAttributes = coercionAnnotatorTextAttributes
//            annotation.gutterIconRenderer = GutterActionRenderer(object : BaseNavigateToSourceAction(false) {
//            })
            annotation.gutterIconRenderer = object : GutterActionRenderer(GotoDeclarationAction()) {
              override fun getIcon() = ArrowIcons.OPTICS
            }
          }
        }
    })


val IdeMetaPlugin.coercionKtValArgAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtValueArgument>()
        ?.takeIf { ctx.isCoerced(it) }
        ?.let { psiElement: KtValueArgument ->
          val message = psiElement.participatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
                html {
                  body {
                    p {
                      "Implicit coercion applied by ${KDocRenderer.renderKDocContent(kDocTag)}"
                    } + p {
                      HTML.withOptions {
                        classifierNamePolicy = HtmlClassifierNamePolicy(ClassifierNamePolicy.SHORT)
                        valueParametersHandler = WrapValueParameterHandler(valueParametersHandler)
                        annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
                        renderCompanionObjectName = true
                        withDefinedIn = false
                        eachAnnotationOnNewLine = true
                        boldOnlyForNamesInHtml = true
                        startFromName = false
                        startFromDeclarationKeyword = false
                        propertyAccessorRenderingPolicy = PropertyAccessorRenderingPolicy.DEBUG
                      }.render(proof.through)
                    }
                  }
                }.render()
              }
              // SourceNavigationHelper.getNavigationElement(proof.through.containingDeclaration)
              // (proof.through.source as KotlinSourceElement).psi.references
            }
          } ?: "Proof not found"
          psiElement.getArgumentExpression()?.let {
            val annotation = holder.createAnnotation(HighlightSeverity.INFORMATION, it.textRange, null, message)
            annotation.enforcedTextAttributes = coercionAnnotatorTextAttributes
            //ViewSourceAction()
            annotation.gutterIconRenderer = object : GutterActionRenderer(GotoDeclarationAction()) {
              override fun getIcon() = ArrowIcons.OPTICS
            }
          }
        }
    }
  )
