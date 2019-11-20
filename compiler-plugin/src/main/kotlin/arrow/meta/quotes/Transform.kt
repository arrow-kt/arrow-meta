package arrow.meta.quotes

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment

/**
 * The resulting action from matching on transformation
 */
sealed class Transform<out K : KtElement> {

  data class Replace<out K : KtElement>(
    val replacing: PsiElement,
    val newDeclarations: List<Scope<KtElement>>,
    val replacementId: String? = null
  ) : Transform<K>()

  data class Remove<out K : KtElement>(
    val removing: PsiElement,
    val declarations: List<Scope<KtExpressionCodeFragment>> = listOf()
  ) : Transform<K>()

  object Empty : Transform<Nothing>()

  companion object {
    fun <K : KtElement> replace(
      replacing: PsiElement,
      newDeclarations: List<Scope<KtElement>>
    ): Transform<K> =
      Replace(replacing, newDeclarations)

    fun <K : KtElement> replace(
      replacing: PsiElement,
      newDeclaration: Scope<KtElement>
    ): Transform<K> =
      replace(replacing, listOf(newDeclaration))

    fun <K : KtElement> remove(remove: PsiElement): Transform<K> = Remove(remove)

    fun <K : KtElement> remove(
      removeIn: PsiElement,
      declaration: Scope<KtExpressionCodeFragment>
    ): Transform<K> = Remove(removeIn, listOf(declaration))

    fun <K : KtElement> remove(
      removeIn: PsiElement,
      declarations: List<Scope<KtExpressionCodeFragment>>
    ): Transform<K> = Remove(removeIn, declarations)

    val empty: Transform<Nothing> = Empty
  }

}
