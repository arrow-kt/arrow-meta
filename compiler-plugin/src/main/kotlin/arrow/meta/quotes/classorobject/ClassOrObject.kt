package arrow.meta.quotes.classorobject

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.element.ClassBody
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.allConstructors
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType

/**
 * A template destructuring [Scope] for a [KtClassOrObject]
 *
 * Parent scope of [KtClass] and [KtObjectDeclaration]
 */
open class ClassOrObjectScope<out T : KtClassOrObject>(
  override val value: T,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val superTypes: ScopedList<KtSuperTypeListEntry> = ScopedList(value = value.superTypeListEntries, prefix = if (value.superTypeListEntries.isEmpty()) "" else " : "),
  val body: ClassBody = ClassBody(value.body),
  val declarations: ScopedList<KtDeclaration> = ScopedList(value = value.declarations, separator = "\n"),
  val primaryConstructor: KtPrimaryConstructor? = value.primaryConstructor,
  val primaryConstructorParameterList: ScopedList<KtParameter> = ScopedList(value = value.primaryConstructorParameters, separator = ", "),
  val secondaryConstructor: ScopedList<KtSecondaryConstructor> = ScopedList(value = value.secondaryConstructors),
  val anonymousInitializers: ScopedList<KtAnonymousInitializer> = ScopedList(value = value.getAnonymousInitializers()),
  val name: Name? = value.nameAsName
  ) : Scope<T>(value)

fun <T: KtClassOrObject> ClassOrObjectScope<T>.getOrCreateBody(): Scope<KtClassBody> = Scope(value.getOrCreateBody())

val <T: KtClassOrObject> ClassOrObjectScope<T>.allConstructors
  get() = ScopedList(value.allConstructors)

