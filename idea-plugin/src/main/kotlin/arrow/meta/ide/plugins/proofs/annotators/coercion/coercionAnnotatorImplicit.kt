package arrow.meta.ide.plugins.proofs.annotators.coercion

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.localQuickFix
import arrow.meta.ide.dsl.utils.registerLocalFix
import arrow.meta.ide.plugins.proofs.implicitParticipatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInsight.daemon.impl.quickfix.GoToSymbolFix
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.implicitCoercion: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element: PsiElement, holder: AnnotationHolder ->
      val ctx = element.project.getService(CompilerContext::class.java)
      element.safeAs<KtDotQualifiedExpression>()
        ?.let { ktDotQualifiedExpression: KtDotQualifiedExpression ->
          ktDotQualifiedExpression.implicitParticipatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              proof.through.findPsi().safeAs<KtNamedDeclaration>()?.let { proofPsi ->
                val htmlMessage = html {
                  body {
                    text("Apply implicit coercion available by") +
                      text(KotlinQuickDocumentationProvider().generateDoc(proofPsi, ktDotQualifiedExpression).orEmpty())
                  }
                }.render()
                val makeCoercionImplicitFix = localQuickFix(
                  familyName = "Make coercion implicit",
                  f = { ktDotQualifiedExpression.replace(ktDotQualifiedExpression.receiverExpression) }
                )
                holder.newAnnotation(HighlightSeverity.WARNING, htmlMessage)
                  .range(ktDotQualifiedExpression.textRange)
                  .tooltip(htmlMessage)
                  .newFix(GoToSymbolFix(proofPsi, "Go to proof: ${proof.through.fqNameSafe.asString()}")).range(ktDotQualifiedExpression.textRange).registerFix()
                  .registerLocalFix(makeCoercionImplicitFix, ktDotQualifiedExpression, htmlMessage, ProblemHighlightType.WARNING).registerFix()
                  .create()
              }
            }
          }
        }
    }
  )
