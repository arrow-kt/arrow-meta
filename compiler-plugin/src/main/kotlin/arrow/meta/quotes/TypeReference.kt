package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * A template destructuring [Scope] for a [KtTypeReference]
 *
 * Note: [KtTypeReference] is called [TypeMirror] in the Java annotation processing API.
 *
 * @param match designed to to feed in any kind of [KtTypeReference] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.typeReference(
  match: KtTypeReference.() -> Boolean,
  map: TypeReference.(KtTypeReference) -> Transform<KtTypeReference>
): ExtensionPhase =
  quote(match, map) { TypeReference(it) }

/**
 * A template destructuring [Scope] for a [TypeReference]
 */
class TypeReference(
  override val value: KtTypeReference,
  val typeElement: Scope<KtTypeElement>? = Scope(value.typeElement), // TODO KtTypeElement scope and quote template
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries)
): Scope<KtTypeReference>(value)