package arrow.meta.quotes.parentscopes

import arrow.meta.quotes.ClassBodyScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import com.intellij.navigation.ItemPresentation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType

open class ClassOrObjectScope<out T : KtClassOrObject>(
  override val value: T,
  val `@annotationEntry`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val superTypeList: KtSuperTypeList? = value.getSuperTypeList(),
  val superTypeListEntries: ScopedList<KtSuperTypeListEntry> = ScopedList(prefix = "<", value = value.superTypeListEntries, postfix = ">"),
  val anonymousInitializers: ScopedList<KtAnonymousInitializer> = ScopedList(value = value.getAnonymousInitializers(), postfix = ","),
  val body: ClassBodyScope = ClassBodyScope(value.body),
  val declarations: ScopedList<KtDeclaration> = ScopedList(value = value.declarations, postfix = ", "),
  val presentation: ItemPresentation = value.presentation
// TODO finish out here, refactor ClasScope to take this shared classOrObject scope
  ) : Scope<T>(value)