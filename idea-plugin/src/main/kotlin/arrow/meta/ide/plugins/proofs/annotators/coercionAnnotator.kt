package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.isCoerced
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInsight.daemon.impl.quickfix.GoToSymbolFix
import com.intellij.codeInsight.documentation.DocumentationManagerUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.p
import org.jetbrains.kotlin.idea.HtmlClassifierNamePolicy
import org.jetbrains.kotlin.idea.WrapValueParameterHandler
import org.jetbrains.kotlin.idea.kdoc.KDocRenderer
import org.jetbrains.kotlin.idea.kdoc.findKDoc
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.renderer.AnnotationArgumentsRenderingPolicy
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer
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
        ?.let { ktProperty: KtProperty ->
          ktProperty.delegateExpressionOrInitializer?.let {
            ktProperty.participatingTypes()?.let { (subtype, supertype) ->
              ctx.coerceProof(subtype, supertype)?.let { proof ->
                val message = proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
                  html {
                    body {
                      p {
                        "Implicit coercion applied by ${KDocRenderer.renderKDocContent(kDocTag)}"
                      } + p {
                        declarationRenderer.render(proof.through)
//                        KotlinQuickDocumentationProvider().getQuickNavigateInfo(
//                          proof.through.findPsi(),
//                          SourceNavigationHelper.getOriginalElement(proof.through.findPsi() as KtDeclaration)
//                        ).orEmpty()
                      }
                    }
                  }.render()
                }
                println("Annotator messageProperty: $message")
                holder.createAnnotation(HighlightSeverity.INFORMATION, ktProperty.delegateExpressionOrInitializer!!.textRange, null, message).apply {
                  enforcedTextAttributes = coercionAnnotatorTextAttributes
                  registerFix(
                    GoToSymbolFix(proof.through.findPsi() as KtDeclaration, proof.through.name.asString()),
                    ktProperty.delegateExpressionOrInitializer!!.textRange
                  )
                }
              }
            }
          }
        }
    }
  )


val IdeMetaPlugin.coercionKtValArgAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtValueArgument>()
        ?.takeIf { ctx.isCoerced(it) }
        ?.let { ktValueArgument: KtValueArgument ->
          ktValueArgument.getArgumentExpression()?.let {
            ktValueArgument.participatingTypes()?.let { (subtype, supertype) ->
              ctx.coerceProof(subtype, supertype)?.let { proof ->
                val message = proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
                  html {
                    body {
                      p {
                        "Implicit coercion applied by ${KDocRenderer.renderKDocContent(kDocTag)}"
                      } + p {
                        declarationRenderer.render(proof.through)
                      }
                    }
                  }.render()
                }
                println("Annotator messageValArgs: $message")
                holder.createAnnotation(HighlightSeverity.INFORMATION, ktValueArgument.textRange, null, message).apply {
                  enforcedTextAttributes = coercionAnnotatorTextAttributes
                  registerFix(
                    GoToSymbolFix(proof.through.findPsi() as KtDeclaration, proof.through.name.asString()),
                    ktValueArgument.textRange
                  )
                }
              }
            }
          }
        }
    }
  )

val IdeMetaPlugin.declarationDocProvider: ExtensionPhase
  get() = addDocumentationProvider(
    quickNavigateInfo = { element, originalElement ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtValueArgument>()
        ?.takeIf { ctx.isCoerced(it) }
        ?.let { psiElement: KtValueArgument ->
          psiElement.participatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              declarationRenderer.render(proof.through)
              val stringBuilder = StringBuilder("some declaration doc provider \n")
              DocumentationManagerUtil.createHyperlink(stringBuilder, declarationRenderer.render(proof.through), proof.through.name.asString(), false)
              stringBuilder.toString()
            }
          }
        }
    },
    documentationElementForLink = { psiManager: PsiManager, link: String, context: PsiElement ->
      val ctx = context.project.getService(CompilerContext::class.java)
      context.safeAs<KtValueArgument>()
        ?.takeIf { ctx.isCoerced(it) }
        ?.let { psiElement: KtValueArgument ->
          psiElement.participatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              (proof.through.findPsi() as KtDeclaration).navigationElement
            }
          }
        }
    }
  )

private val declarationRenderer: DescriptorRenderer = HTML.withOptions {
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
}
