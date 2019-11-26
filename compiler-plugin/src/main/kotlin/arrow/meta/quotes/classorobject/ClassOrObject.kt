package arrow.meta.quotes.classorobject

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.element.ParameterList
import arrow.meta.quotes.modifierlist.ModifierList
import org.jetbrains.kotlin.com.intellij.navigation.ItemPresentation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
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
  val superTypeList: KtSuperTypeList? = value.getSuperTypeList(),
  val superTypeListEntries: ScopedList<KtSuperTypeListEntry> = ScopedList(prefix = "<", value = value.superTypeListEntries, postfix = ">"),
  val anonymousInitializers: ScopedList<KtAnonymousInitializer> = ScopedList(value = value.getAnonymousInitializers(), postfix = ","),
  val body: ClassBody = ClassBody(value.body),
  val declarations: ScopedList<KtDeclaration> = ScopedList(value = value.declarations, postfix = ", "),
  val presentation: ItemPresentation? = value.presentation,
  val primaryConstructor: KtPrimaryConstructor? = value.primaryConstructor,
  val primaryConstructorModifierList: ModifierList = ModifierList(primaryConstructor?.modifierList),
  val primaryConstructorParameterList: ParameterList = ParameterList(primaryConstructor?.valueParameterList),
  val name: Name? = value.nameAsName
  ) : Scope<T>(value)

fun <T: KtClassOrObject> ClassOrObjectScope<T>.getOrCreateBody(): Scope<KtClassBody> = Scope(value.getOrCreateBody())

val <T: KtClassOrObject> ClassOrObjectScope<T>.allConstructors
  get() = ScopedList(value.allConstructors)

