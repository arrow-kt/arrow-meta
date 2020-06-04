package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import java.awt.event.MouseEvent
import javax.swing.Icon

/**
 * Similar to [arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax.addLineMarkerProvider], is an extension for
 * PsiElements that are not leafs so it will look for the first Leaf corresponding the targeted psiElement
 */
@Suppress("UNCHECKED_CAST")
fun <A : PsiElement> IdeMetaPlugin.addLineMarkerProvider1(
  icon: Icon,
  transform: (PsiElement) -> A?,
  composite: Class<A>,
  message: DescriptorRenderer.Companion.(A) -> String = Noop.string2(),
  placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
  navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
  clickAction: AnAction? = null
): ExtensionPhase =
  addLineMarkerProvider(
    { PsiTreeUtil.findChildOfType(transform(it), LeafPsiElement::class.java) },
    {
      it.onComposite(composite) { psi: A ->
        lineMarkerInfo(icon, it, { message(DescriptorRenderer.Companion, psi) }, placed, navigate, clickAction)
      }
    }
  )

/**
 * Similar to [arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax.addLineMarkerProviderM], is an extension for
 * PsiElements that are not leafs so it will look for the first Leaf corresponding the targeted psiElement
 */
internal fun <A : PsiElement> IdeMetaPlugin.addLineMarkerProviderM(
  icon: Icon,
  transform: (PsiElement) -> A?,
  composite: Class<A>,
  message: DescriptorRenderer.Companion.(A) -> String = Noop.string2(),
  commonIcon: MergeableLineMarkerInfo<PsiElement>.(others: List<MergeableLineMarkerInfo<PsiElement>>) -> Icon = { icon },
  mergeWith: MergeableLineMarkerInfo<PsiElement>.(other: MergeableLineMarkerInfo<*>) -> Boolean = { this.icon == it.icon },
  navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
  placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
  clickAction: AnAction? = null
): ExtensionPhase =
  addLineMarkerProvider(
    { PsiTreeUtil.findChildOfType(transform(it), LeafPsiElement::class.java) },
    {
      it.onComposite(composite) { psi: A ->
        mergeableLineMarkerInfo(icon, it, { message(DescriptorRenderer.Companion, psi) }, commonIcon, mergeWith, placed, navigate, clickAction)
      }
    }
  )
