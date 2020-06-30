package arrow.meta.ide.dsl.editor.lineMarker

import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.presentation.java.SymbolPresentationUtil

abstract class DefaultListCellRenderer<A : PsiElement> : PsiElementListCellRenderer<A>() {
  override fun getContainerText(element: A, name: String): String? =
    SymbolPresentationUtil.getSymbolContainerText(element)

  override fun getIconFlags(): Int = Iconable.ICON_FLAG_VISIBILITY

  override fun getElementText(element: A): String =
    SymbolPresentationUtil.getSymbolPresentableText(element)

  companion object {
    fun <A : PsiElement> default(): DefaultListCellRenderer<A> =
      object : DefaultListCellRenderer<A>() {}
  }
}