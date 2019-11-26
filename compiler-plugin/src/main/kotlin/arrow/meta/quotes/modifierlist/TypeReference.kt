package arrow.meta.quotes.modifierlist

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * A template destructuring [Scope] for a [TypeReference]
 */
class TypeReference(
  override val value: KtTypeReference?,
  val typeElement: Scope<KtTypeElement>? = Scope(value?.typeElement), // TODO KtTypeElement scope and quote template
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries?: listOf())
): Scope<KtTypeReference>(value)