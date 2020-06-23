package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.folding.getType
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
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
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.awt.event.MouseEvent

val IdeMetaPlugin.givenParamLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.ICON1,
    composite = KtParameter::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtParameter>()?.takeIf { it.isGivenProof() }
    },
    message = { ktParameter: KtParameter ->
      ktParameter.givenMessage(ktParameter.ctx())
    },
    navigate = { _: MouseEvent, element: PsiElement ->
      element.navigateToProof(element.ctx())
    }
  )

private fun KtParameter.givenMessage(ctx: CompilerContext?): String =
  typeReference?.getType()?.let { kotlinType ->
    ctx?.givenProof(kotlinType)?.let { proof ->
      proof.through.findPsi()?.let { proofPsi ->
        html {
          body {
            text("${nameIdentifier?.text} is implicitly injected by given proof unless explicitly passed as argument at the use site") +
              text(KotlinQuickDocumentationProvider().generateDoc(proofPsi as KtNamedDeclaration, this).orEmpty())
          }
        }.render().trimIndent()
      }
    }
  }.orEmpty()

private fun PsiElement.navigateToProof(ctx: CompilerContext?) =
  getParentOfType<KtParameter>(true)?.typeReference?.getType()?.let { kotlinType ->
    ctx?.givenProof(kotlinType)?.let { proof ->
      proof.through.findPsi()?.let { proofPsi ->
        PsiNavigateUtil.navigate(proofPsi)
      }
    }
  }
