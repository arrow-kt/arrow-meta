package arrow.meta.ide.dsl.editor.icon

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.structureView.StructureViewSyntax
import arrow.meta.ide.dsl.utils.isNotNull
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.ide.IconProvider
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.KotlinIconProvider
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import javax.swing.Icon

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
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.psi.PsiElement
   * import org.jetbrains.kotlin.idea.KotlinIcons
   * import org.jetbrains.kotlin.psi.KtFile
   * import org.jetbrains.kotlin.psi.KtObjectDeclaration
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * val IdeMetaPlugin.fileAndStructureViewIcons: IdePlugin
   *   get() = "File- and StructureViewIcons" {
   *     meta(
   *       addIcon(KotlinIcons.GRADLE_SCRIPT) { psi: PsiElement, _: Int ->
   *         psi.safeAs<KtFile>()?.takeIf { it.isScript() && it.name.endsWith(".gradle.kts") }
   *       },
   *       addIcon(KotlinIcons.OBJECT) { psi, _ ->
   *         psi.safeAs<KtObjectDeclaration>()
   *       }
   *     )
   *   }
   * ```
   * This implementation creates 2 [IconProvider]s. The first registers the [KotlinIcons.GRADLE_SCRIPT] Icon to any Kotlin ScriptFile, which ends with `.gradle.kts`.
   * The other registers [KotlinIcons.OBJECT] Icon to any [KtObjectDeclaration], so that it appears in the StructureView.
   * The advantage of registering multiple [IconProvider]s than one, which orchestrate all possible Icons, is that it is easier to Debug and Test each `Icon` specifically.
   * The parameter `flag` from [transform] is used to compose more complex [transform] functions considering [Iconable.ICON_FLAG_VISIBILITY], [Iconable.ICON_FLAG_IGNORE_MASK] or [Iconable.ICON_FLAG_READ_STATUS].
   */
  fun <A : PsiElement> IdeMetaPlugin.addIcon(
    icon: Icon,
    transform: (psiElement: PsiElement, flag: Int) -> A? = Noop.nullable2()
  ): ExtensionPhase =
    extensionProvider(
      IconProvider.EXTENSION_POINT_NAME,
      iconProvider { p0, p1 -> transform(p0, p1)?.run { icon } },
      LoadingOrder.FIRST
    )

  /**
   * registers an [IconProvider]
   * `TransformIcon<A>` is an alias for `Pair<Icon, (psiElement: PsiElement, flag: Int) -> A?>`
   * If only one [IconProvider] is desired, we may use [addIcons] and create those `Pairs` with [icon].
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.invoke
   * import org.jetbrains.kotlin.idea.KotlinIcons
   * import org.jetbrains.kotlin.psi.KtFile
   * import org.jetbrains.kotlin.psi.KtObjectDeclaration
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * val IdeMetaPlugin.fileAndStructureViewIcons: IdePlugin
   *   get() = "File- and StructureViewIcons" {
   *     meta(
   *       addIcons(
   *         icon(KotlinIcons.GRADLE_SCRIPT) { psi, _ ->
   *           psi.safeAs<KtFile>()?.takeIf { it.isScript() && it.name.endsWith(".gradle.kts") }
   *         },
   *         icon(KotlinIcons.OBJECT) { psi, _ ->
   *           psi.safeAs<KtObjectDeclaration>()
   *         }
   *       )
   *     )
   *   }
   * ```
   * @see IconProviderSyntax
   * @see addIcon
   */
  fun <A : PsiElement> IdeMetaPlugin.addIcons(vararg values: TransformIcon<A>): ExtensionPhase =
    extensionProvider(
      IconProvider.EXTENSION_POINT_NAME,
      iconProvider { p0, p1 -> values.toList().firstOrNull { isNotNull(it.second(p0, p1)) }?.run { first } },
      LoadingOrder.FIRST
    )

  fun <A : PsiElement> IconProviderSyntax.icon(icon: Icon, transform: (psiElement: PsiElement, flag: Int) -> A? = Noop.nullable2()): TransformIcon<A> =
    icon to transform

  fun IconProviderSyntax.iconProvider(
    transform: (psiElement: PsiElement, flag: Int) -> Icon? = Noop.nullable2()
  ): IconProvider =
    object : IconProvider(), DumbAware {
      override fun getIcon(p0: PsiElement, p1: Int): Icon? =
        transform(p0, p1)
    }
}

typealias TransformIcon<A> = Pair<Icon, (psiElement: PsiElement, flag: Int) -> A?>
