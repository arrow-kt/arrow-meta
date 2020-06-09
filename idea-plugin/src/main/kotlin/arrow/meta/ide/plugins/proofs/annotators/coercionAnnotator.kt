package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.coercionMessage
import arrow.meta.ide.plugins.proofs.markers.isCoerced
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.coercionAnnotator: ExtensionPhase
  get() = Composite(
    coercionKtPropertyAnnotator,
    coercionKtValArgAnnotator
  )

val IdeMetaPlugin.coercionKtPropertyAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      element.safeAs<KtProperty>()?.takeIf { psiElement: KtProperty ->
        //true
        psiElement.ctx().isCoerced(psiElement)
      }?.let { psiElement: KtProperty ->
        val message = psiElement.participatingTypes()?.let { (subtype, supertype) ->
          //"some message"
          psiElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
        } ?: "Proof not found"
        psiElement.delegateExpressionOrInitializer?.let {
          holder.createInfoAnnotation(it, message)
            .textAttributes = KotlinHighlightingColors.SMART_CAST_VALUE
        }
      }
    }
  )

val IdeMetaPlugin.coercionKtValArgAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      element.safeAs<KtValueArgument>()?.takeIf { psiElement: KtValueArgument ->
        //true
        psiElement.ctx().isCoerced(psiElement)
      }?.let { psiElement: KtValueArgument ->
        val message = psiElement.participatingTypes()?.let { (subtype, supertype) ->
          //"some message"
          psiElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
        } ?: "Proof not found"
        psiElement.getArgumentExpression()?.let {
          holder.createInfoAnnotation(it, message)
            .textAttributes = KotlinHighlightingColors.SMART_CAST_VALUE
        }
      }
    }
  )
