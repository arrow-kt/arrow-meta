package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
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
fun <A : PsiElement> IdeMetaPlugin.addLineMarkerProvider(
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

@Suppress("UNCHECKED_CAST")
fun <A : PsiElement, B : PsiElement> IdeMetaPlugin.addRelatedLineMarkerProvider(
  icon: Icon,
  transform: (PsiElement) -> A?,
  composite: Class<A>,
  targets: (A) -> List<B>,
  message: DescriptorRenderer.Companion.(A, targets: List<B>) -> String? = Noop.nullable3(),
  cellRenderer: PsiElementListCellRenderer<B> = DefaultPsiElementCellRenderer() as PsiElementListCellRenderer<B>,
  popUpTitle: DescriptorRenderer.Companion.(A, targets: List<B>) -> String? = Noop.string3(),
  placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
): ExtensionPhase =
  relatedLineMarkerProvider(
    { PsiTreeUtil.findChildOfType(transform(it), LeafPsiElement::class.java) },
    {
      it.onComposite(composite) { a: A ->
        navigateGutter(
          icon,
          a,
          targets,
          { a: A, list: List<B> -> message(DescriptorRenderer.Companion, a, list) },
          cellRenderer,
          { a: A, list: List<B> -> popUpTitle(DescriptorRenderer.Companion, a, list) },
          placed
        ).invoke(it)
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
