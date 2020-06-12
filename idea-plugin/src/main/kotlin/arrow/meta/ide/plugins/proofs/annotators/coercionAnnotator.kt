package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.isCoerced
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.IdeDescriptorRenderers
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
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              // proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
              //   COMPACT.renderMessage("Implicit coercion applied by ${KDocRenderer.renderKDocContent(kDocTag)}")
              //}
              // SourceNavigationHelper.getNavigationElement(proof.through.containingDeclaration)
              "Implicit coercion applied by \n" +
                IdeDescriptorRenderers.SOURCE_CODE.render(proof.through)
            }
          } ?: "Proof not found"
          psiElement.delegateExpressionOrInitializer?.let {
            holder.createAnnotation(HighlightSeverity.INFORMATION, it.textRange, message, message)
              .enforcedTextAttributes = coercionAnnotatorTextAttributes
//            holder.createInfoAnnotation(it, message).apply {
//              enforcedTextAttributes = coercionAnnotatorTextAttributes
//              registerFix(object : IntentionAction{
//                override fun startInWriteAction(): Boolean {
//                  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                }
//
//                override fun getFamilyName(): String {
//                  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                }
//
//                override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
//                  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                }
//
//                override fun getText(): String {
//                  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                }
//
//                override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
//                  psiElement.participatingTypes()?.let { (subtype, supertype) ->
//                    ctx.coerceProof(subtype, supertype)?.let { proof ->
//                      PsiNavigateUtil.navigate(proof.through.findPsi())
//                    }
//                  }
//                }
//              })
//            }
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
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              //  proof.through.containingDeclaration.findKDoc { proof.through.findPsi() }?.let { kDocTag: KDocTag ->
              //    COMPACT.renderMessage("Implicit coercion applied by ${KDocRenderer.renderKDocContent(kDocTag)}\n " +
              //      html { body { a("some text link", "https://www.47deg.com/") } }.render())
              //  }
              "Implicit coercion applied by \n" +
                IdeDescriptorRenderers.SOURCE_CODE.render(proof.through)
            }
          } ?: "Proof not found"
          psiElement.getArgumentExpression()?.let {
            holder.createAnnotation(HighlightSeverity.INFORMATION, it.textRange, message, message)
              .enforcedTextAttributes = coercionAnnotatorTextAttributes
          }
        }
    }
  )
