package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * A template destructuring [Scope] for a [KtTypeReference]
 */
fun Meta.typeReference(
  match: KtTypeReference.() -> Boolean,
  map: TypeReferenceScope.(KtTypeReference) -> Transform<KtTypeReference>
): ExtensionPhase =
  quote(match, map) { TypeReferenceScope(it) }

/**
 * A template destructuring [Scope] for a [TypeReferenceScope]
 */
class TypeReferenceScope(
  override val value: KtTypeReference,
  val typeElement: Scope<KtTypeElement>? = Scope(value.typeElement), // TODO KtTypeElement scope and quote template
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries)
  ): Scope<KtTypeReference>(value)