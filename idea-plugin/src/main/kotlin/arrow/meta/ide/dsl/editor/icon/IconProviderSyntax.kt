package arrow.meta.ide.dsl.editor.icon

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.ide.IdeMetaPlugin
import com.intellij.ide.IconProvider
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import javax.swing.Icon
import arrow.meta.ide.dsl.editor.structureView.StructureViewSyntax
import com.intellij.openapi.util.Iconable
import org.jetbrains.kotlin.idea.KotlinIconProvider
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * [IconProvider] display Icon's for Files and the StructureView.
 * [StructureViewSyntax] provides APIs to create StructureViews.
 * Check out the [Docs](https://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html?search=icon).
 */
interface IconProviderSyntax {

  /**
   * registers an [IconProvider].
   * One minimal example from [KotlinIconProvider], may look like this:
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import com.intellij.psi.PsiElement
   * import org.jetbrains.kotlin.idea.KotlinIcons
   * import org.jetbrains.kotlin.psi.KtFile
   * import org.jetbrains.kotlin.psi.KtObjectDeclaration
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * val IdeMetaPlugin.fileAndStructureViewIcons: Plugin
   *  get() = "File- and StructureViewIcons" {
   *   meta(
   *    addIcon(KotlinIcons.GRADLE_SCRIPT) { psi: PsiElement, _: Int ->
   *      psi.safeAs<KtFile>()?.takeIf { it.isScript() && it.name.endsWith(".gradle.kts") }
   *    },
   *    addIcon(KotlinIcons.OBJECT) { psi, _ ->
   *      psi.safeAs<KtObjectDeclaration>()
   *    }
   *   )
   *  }
   * ```
   * This implementation creates 2 [IconProvider]s. The first registers the [KotlinIcons.GRADLE_SCRIPT] Icon to any Kotlin ScriptFile, which ends with `.gradle.kts`.
   * The other registers [KotlinIcons.OBJECT] Icon to any [KtObjectDeclaration], so that it appears in the StructureView.
   * The advantage of registering multiple [IconProvider]s than one, which orchestrate all possible Icons, is that it is easier to Debug and Test each `Icon` specifically.
   * The parameter `flag` from [transform] is used to compose more complex [transform] functions considering [Iconable.ICON_FLAG_VISIBILITY], [Iconable.ICON_FLAG_IGNORE_MASK] or [Iconable.ICON_FLAG_READ_STATUS].
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
