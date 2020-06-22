package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.returnType
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.givenProof
import com.intellij.psi.PsiElement
import com.intellij.util.PsiNavigateUtil
import org.celtric.kotlin.html.body
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.text
import org.jetbrains.kotlin.idea.KotlinQuickDocumentationProvider
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.awt.event.MouseEvent

val IdeMetaPlugin.givenCallLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.ICON1,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtCallExpression>()?.takeIf { element: KtCallExpression ->
        element.referenceExpression()?.text == "given"
      }?.referenceExpression()?.firstChild
    },
    message = { element: PsiElement ->
      element.message(element.ctx())
    },
    navigate = { _: MouseEvent, element: PsiElement ->
      element.navigateToProof(element.ctx())
    }
  )

private fun PsiElement.message(ctx: CompilerContext?, extra: String = ""): String =
  html {
    body {
      getParentOfType<KtCallExpression>(true)?.returnType?.let { kotlinType ->
        ctx?.givenProof(kotlinType)?.through?.findPsi()?.let { proofPsi ->
          text("Implicit injection by given proof ($extra)") +
            text(KotlinQuickDocumentationProvider().generateDoc(proofPsi as KtNamedDeclaration, this).orEmpty())
        }
      }.orEmpty()
    }
  }.render().trimIndent()

private fun PsiElement.navigateToProof(ctx: CompilerContext?) =
  getParentOfType<KtCallExpression>(true)?.returnType?.let { kotlinType ->
    ctx?.givenProof(kotlinType)?.let { proof ->
      proof.through.findPsi()?.let { proofPsi ->
        PsiNavigateUtil.navigate(proofPsi)
      }
    }
  }
