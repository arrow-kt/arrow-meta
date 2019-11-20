package arrow.meta.quotes

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement

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
    val declarations: List<Scope<KtElement>> = listOf()
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
      remove: PsiElement,
      declaration: Scope<KtElement>
    ): Transform<K> = Remove(remove, listOf(declaration))

    fun <K : KtElement> remove(
      remove: PsiElement,
      declarations: List<Scope<KtElement>>
    ): Transform<K> = Remove(remove, declarations)

    val empty: Transform<Nothing> = Empty
  }

}
