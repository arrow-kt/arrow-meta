package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.folding.getType
import arrow.meta.ide.plugins.proofs.psi.isGivenProof
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.givenProof
import com.intellij.codeInsight.daemon.impl.quickfix.GoToSymbolFix
import com.intellij.ide.actions.GotoSymbolAction
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.awt.event.MouseEvent

val IdeMetaPlugin.givenParamLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.ICON1,
    composite = KtParameter::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtParameter>()?.takeIf { it.isGivenProof() }
    },
    message = { decl: KtParameter ->
      decl.markerMessage(decl.ctx())
    },
    navigate = { event: MouseEvent, element: PsiElement ->
      element.getParentOfType<KtParameter>(true)?.let {
        it.typeReference?.getType()?.let { kotlinType ->
          it.ctx()?.givenProof(kotlinType)?.let { proof ->
            proof.through.findPsi()?.let { proofPsi ->
              GoToSymbolFix(proofPsi as KtDeclaration, "Go to proof: ${proof.through.fqNameSafe.asString()}")
            }
          }
        }
      }
    }
  )
