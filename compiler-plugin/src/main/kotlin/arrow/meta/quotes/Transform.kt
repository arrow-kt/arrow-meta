package arrow.meta.quotes

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment

/**
 * The resulting action from matching on transformation
 */
sealed class Transform<out K : KtElement> {

  /**
   * A Transform that replaces some element in AST. See below:
   *
   * ```kotlin:ank:silent
   * import arrow.meta.Meta
   * import arrow.meta.Plugin
   * import arrow.meta.invoke
   * import arrow.meta.quotes.Transform
   * import arrow.meta.quotes.namedFunction
   *
   * val Meta.replacePlugin: Plugin
   *  get() =
   *   "Replace Transform" {
   *     meta(
   *      namedFunction({ name == "helloWorld" }) { c ->
   *        Transform.replace(
   *          replacing = c,
   *          newDeclaration =
   *          """|fun helloWorld(): Unit =
   *             |  println("Hello ΛRROW Meta!")
   *             |""".function.synthetic
   *        )
   *      }
   *     )
   *   }
   * ```
   *
   * @param replacing the element to be replaced
   * @param newDeclarations are the elements that will replace
   */
  data class Replace<out K : KtElement>(
    val replacing: PsiElement,
    val newDeclarations: List<Scope<KtElement>>,
    val replacementId: String? = null
  ) : Transform<K>()

  /**
   * A Transform that removes declarations from a specific element in the AST. See below:
   *
   * ```kotlin:ank:silent
   * import arrow.meta.Meta
   * import arrow.meta.Plugin
   * import arrow.meta.invoke
   * import arrow.meta.quotes.Transform
   * import arrow.meta.quotes.namedFunction
   *
   * val Meta.replacePlugin: Plugin
   *  get() =
   *   "Remove Transform" {
   *     meta(
   *      namedFunction({ name == "helloWorld" }) { c ->
   *        Transform.remove(
   *          remove = c
   *        )
   *      }
   *     )
   *   }
   * ```
   *
   * @param removing is the element context
   * @param declarations are the elements that should be removed
   */
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

    fun <K : KtElement> remove(remove: PsiElement): Transform<K> = Replace(remove, emptyList())

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
