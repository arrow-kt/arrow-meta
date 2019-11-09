package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtParameter] [Quote] with a custom template destructuring [ParameterScope]
 */
fun Meta.parameter(
  match: KtParameter.() -> Boolean,
  map: ParameterScope.(KtParameter) -> Transform<KtParameter>
) : ExtensionPhase =
  quote(match, map) { ParameterScope(it) }

/**
 * A template destructuring [Scope] for a [KtParameter]
 */
class ParameterScope(
  override val value: KtParameter,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value.nameAsName,
  val `@annotationEntries`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries)
) : Scope<KtParameter>(value)