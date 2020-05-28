package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.awt.event.MouseEvent
import javax.swing.Icon

val IdeMetaPlugin.implicitCoercionValueArgumentLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.ICON4,
    composite = KtValueArgument::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtValueArgument>()?.takeIf {
        psiElement.ctx().isCoerced(it)
      }
    },
    message = { ktElement: KtValueArgument ->
      ktElement.participatingTypes()?.let { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      } ?: "Proof not found"
    }
  )
