package arrow.meta.ide.plugins.proofs.annotators.coercion

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.localQuickFix
import arrow.meta.ide.dsl.utils.registerLocalFix
import arrow.meta.ide.plugins.proofs.explicit
import arrow.meta.ide.plugins.proofs.isCoerced
import arrow.meta.ide.plugins.proofs.utils.implicitProofAnnotatorTextAttributes
import arrow.meta.ide.plugins.proofs.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInsight.daemon.impl.quickfix.GoToSymbolFix
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinDocumentationProvider
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.explicitPropertyCoercion: Annotator
  get() = Annotator { element: PsiElement, holder: AnnotationHolder ->
    val ctx = element.project.getService(CompilerContext::class.java)
    element.safeAs<KtProperty>()
      ?.takeIf { ctx.isCoerced(it) }
      ?.let { ktProperty: KtProperty ->
        ktProperty.delegateExpressionOrInitializer?.let {
          ktProperty.participatingTypes()?.let { (subtype, supertype) ->
            ctx.coerceProof(subtype, supertype)?.let { proof ->
              proof.through.findPsi().safeAs<KtNamedDeclaration>()?.let { proofPsi ->
                val htmlMessage = html {
                  body {
                    text("Implicit coercion applied by") +
                      text(KotlinDocumentationProvider().generateDoc(proofPsi, ktProperty)
                        .orEmpty())
                  }
                }.render()
                val makeCoercionExplicitFix = localQuickFix(
                  message = "Make coercion explicit",
                  f = { ctx.explicit(ktProperty) }
                )
                holder.newAnnotation(HighlightSeverity.INFORMATION, htmlMessage)
                  .range(it.textRange)
                  .tooltip(htmlMessage)
                  .enforcedTextAttributes(implicitProofAnnotatorTextAttributes)
                  .newFix(GoToSymbolFix(proofPsi, "Go to proof: ${proof.through.fqNameSafe.asString()}")).range(it.textRange).registerFix()
                  .registerLocalFix(makeCoercionExplicitFix, ktProperty, htmlMessage).registerFix()
                  .create()
              }
            }
          }
        }
      }
  }
