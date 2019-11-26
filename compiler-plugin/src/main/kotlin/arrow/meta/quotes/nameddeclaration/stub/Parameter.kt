package arrow.meta.quotes.nameddeclaration.stub

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A template destructuring [Scope] for a [KtParameter]
 */
class Parameter(
  override val value: KtParameter?,
  val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value?.nameAsName,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries
    ?: listOf()),
  val type: Scope<KtTypeReference> = Scope(value?.typeReference),
  val `(typeParams)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value?.typeParameters
    ?: listOf(), postfix = ">"),
  val defaultValue: Scope<KtExpression> = Scope(value?.defaultValue)
) : Scope<KtParameter>(value)