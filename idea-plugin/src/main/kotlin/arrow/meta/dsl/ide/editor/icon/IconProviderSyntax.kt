package arrow.meta.dsl.ide.editor.icon

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.ide.IconProvider
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import javax.swing.Icon

interface IconProviderSyntax {
  /**
   * For emitting FileIcons or Icons in the StructureView
   * check here: https://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html?search=icon
   */
  fun <A : PsiElement> IdeMetaPlugin.addIcon(
    icon: Icon? = null,
    transform: (psiElement: PsiElement, flag: Int) -> A? = Noop.nullable2()
  ): ExtensionPhase =
    extensionProvider(
      IconProvider.EXTENSION_POINT_NAME,
      object : IconProvider(), DumbAware {
        override fun getIcon(p0: PsiElement, p1: Int): Icon? =
          transform(p0, p1)?.run { icon }
      },
      LoadingOrder.FIRST
    )
}
