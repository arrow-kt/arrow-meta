package arrow.meta.quotes

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement

/**
 * The property scope used in destructuring templates & the [arrow.meta.phases.analysis.ElementScope] DSL
 */
open class Scope<out K : KtElement>(open val value: K?) {

  val psiElement: MutableList<PsiElement> = mutableListOf()

  operator fun <K: KtElement> ScopedList<K>.rangeTo(other: String): Name =
    Name.identifier((value.map { it.text } + other).joinToString(", "))

  override fun toString(): String =
    if (value != null) value?.text ?: "" else "" //

  companion object{
    fun <A> empty() = Scope(null)
  }// java null snicking in
}

fun <K : KtElement> Scope<K>?.orEmpty(): Scope<K> =
  this ?: Scope.empty<K>()

fun <K : KtElement> K?.scope(): Scope<K> =
  Scope(this)
