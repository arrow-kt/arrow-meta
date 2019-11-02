package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtTypeAlias] [Quote] with a custom template destructuring [TypeAliasScope]
 */
fun Meta.typeAlias(
  match: KtTypeAlias.() -> Boolean,
  map: TypeAliasScope.(KtTypeAlias) -> Transform<KtTypeAlias>
): ExtensionPhase =
  quote(match, map) { TypeAliasScope(it) }

/**
 * A template destructuring [Scope] for a [KtTypeAlias]
 */
class TypeAliasScope(
  override val value: KtTypeAlias,
  val `@annotationEntries`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value.nameAsName,
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val type: Scope<KtTypeReference> = Scope(value.getTypeReference())
  ) : Scope<KtTypeAlias>(value)
