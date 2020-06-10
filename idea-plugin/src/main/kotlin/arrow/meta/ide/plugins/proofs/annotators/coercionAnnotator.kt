package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.coercionMessage
import arrow.meta.ide.plugins.proofs.markers.isCoerced
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
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
            ctx.coerceProof(subtype, supertype)?.coercionMessage()
          } ?: "Proof not found"
          psiElement.delegateExpressionOrInitializer?.let {
            holder.createInfoAnnotation(it, message).enforcedTextAttributes = coercionAnnotatorTextAttributes
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
        ?.let { psiElement: KtValueArgument ->
          val message = psiElement.participatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.coercionMessage()
          } ?: "Proof not found"
          psiElement.getArgumentExpression()?.let {
            holder.createInfoAnnotation(it, message).enforcedTextAttributes = coercionAnnotatorTextAttributes
          }
        }
    }
  )
