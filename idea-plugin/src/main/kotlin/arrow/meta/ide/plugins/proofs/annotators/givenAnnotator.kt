package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.returnType
import arrow.meta.ide.plugins.proofs.folding.getType
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.givenProof
import com.intellij.codeInsight.daemon.impl.quickfix.GoToSymbolFix
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.givenAnnotator: ExtensionPhase
  get() = Composite(
    givenParamAnnotator,
    givenCallAnnotator
  )

val IdeMetaPlugin.givenParamAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtParameter>()
        ?.takeIf { it.isGivenProof() }
        ?.let { ktParameter: KtParameter ->
          ktParameter.typeReference?.getType()?.let { kotlinType ->
            ctx?.givenProof(kotlinType)?.let { proof ->
              proof.through.findPsi().safeAs<KtNamedDeclaration>()?.let { proofPsi ->
                val htmlMessage = html {
                  body {
                    text("${ktParameter.nameIdentifier?.text} is implicitly injected by given proof unless explicitly passed as argument at the use site") +
                      text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, ktParameter).orEmpty())
                  }
                }.render()
                holder.newAnnotation(HighlightSeverity.INFORMATION, htmlMessage)
                  .range(ktParameter.textRange)
                  .tooltip(htmlMessage)
                  .enforcedTextAttributes(implicitProofAnnotatorTextAttributes)
                  .newFix(GoToSymbolFix(proofPsi, "Go to proof: ${proof.through.fqNameSafe.asString()}")).range(ktParameter.textRange).registerFix()
                  .create()
              }
            }
          }
        }
    }
  )

val IdeMetaPlugin.givenCallAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtCallExpression>()?.takeIf {
        it.referenceExpression()?.text == "given"
      }?.let { ktCallExpression: KtCallExpression ->
        ktCallExpression.returnType?.let { kotlinType ->
          ctx?.givenProof(kotlinType)?.let { proof ->
            proof.through.findPsi().safeAs<KtNamedDeclaration>()?.let { proofPsi ->
              val htmlMessage = html {
                body {
                  text("Implicit injection by given proof") +
                    text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, ktCallExpression).orEmpty())
                }
              }.render()
              holder.newAnnotation(HighlightSeverity.INFORMATION, htmlMessage)
                .range(ktCallExpression.textRange)
                .tooltip(htmlMessage)
                .enforcedTextAttributes(implicitProofAnnotatorTextAttributes)
                .newFix(GoToSymbolFix(proofPsi, "Go to proof: ${proof.through.fqNameSafe.asString()}")).range(ktCallExpression.textRange).registerFix()
                .create()
            }
          }
        }
      }
    }
  )
