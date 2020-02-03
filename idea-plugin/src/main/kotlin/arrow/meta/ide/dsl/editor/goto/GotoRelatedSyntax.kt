package arrow.meta.ide.dsl.editor.goto

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.navigation.GotoRelatedItem
import com.intellij.navigation.GotoRelatedProvider
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiElement
import com.intellij.util.PsiNavigateUtil
import javax.swing.Icon

/**
 * [GotoRelatedSyntax]
 */
interface GotoRelatedSyntax {
  /**
   * one example is here org.jetbrains.kotlin.idea.goto.KotlinExpectOrActualGotoRelatedProvider
   */
  fun IdeMetaPlugin.addGotoRelatedProvider(
    psiBased: (psi: PsiElement) -> List<GotoRelatedItem> = Noop.emptyList1(),
    ctxBased: (ctx: DataContext) -> List<GotoRelatedItem> = Noop.emptyList1()
  ): ExtensionPhase =
    extensionProvider(
      GotoRelatedProvider.EP_NAME,
      object : GotoRelatedProvider() {
        override fun getItems(psiElement: PsiElement): List<GotoRelatedItem> =
          psiBased(psiElement)

        override fun getItems(context: DataContext): List<GotoRelatedItem> =
          ctxBased(context)
      }
    )
}

inline fun <reified A : PsiElement> GotoRelatedSyntax.gotoRelatedItem(
  psi: A,
  group: String = GotoRelatedItem.DEFAULT_GROUP_NAME,
  mnemonic: Int = -1,
  noinline navigate: (A) -> Unit = { PsiNavigateUtil.navigate(it) },
  noinline costumName: (A) -> String? = Noop.nullable1(),
  noinline costumIcon: (A) -> Icon? = Noop.nullable1()
): GotoRelatedItem =
  object : GotoRelatedItem(psi) {
    override fun navigate(): Unit = navigate(psi)

    override fun getGroup(): String = group

    override fun getCustomName(): String? = costumName(psi)

    override fun getCustomIcon(): Icon? = costumIcon(psi)

    override fun getMnemonic(): Int = mnemonic
  }
