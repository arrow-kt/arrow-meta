package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.isCoerced
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInsight.daemon.impl.quickfix.GoToSymbolFix
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.awt.Color
import java.awt.Font

val IdeMetaPlugin.coercionAnnotator: ExtensionPhase
  get() = Composite(
    coercionKtPropertyAnnotator,
    coercionKtValArgAnnotator
  )

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
                proof.through.findPsi()?.let { proofPsi ->
                  val message = html {
                    body {
                      text("Implicit coercion applied by") +
                        text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, ktProperty)
                          .orEmpty())
                    }
                  }.render()
                  holder.createAnnotation(HighlightSeverity.INFORMATION, it.textRange, null, message).apply {
                    enforcedTextAttributes = coercionAnnotatorTextAttributes
                    registerFix(
                      GoToSymbolFix(proofPsi as KtDeclaration, "Go to proof: ${proof.through.fqNameSafe.asString()}"),
                      it.textRange
                    )
                  }
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
                proof.through.findPsi()?.let { proofPsi ->
                  val message = html {
                    body {
                      text("Implicit coercion applied by") +
                        text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, ktValueArgument)
                          .orEmpty())
                    }
                  }.render()
                  holder.createAnnotation(HighlightSeverity.INFORMATION, it.textRange, null, message).apply {
                    enforcedTextAttributes = coercionAnnotatorTextAttributes
                    registerFix(
                      GoToSymbolFix(proofPsi as KtDeclaration, "Go to proof: ${proof.through.fqNameSafe.asString()}"),
                      it.textRange
                    )
                  }
                }
              }
            }
          }
        }
    }
  )

private val coercionAnnotatorTextAttributes =
  TextAttributes(null, null, Color(192, 192, 192), EffectType.WAVE_UNDERSCORE, Font.PLAIN)
