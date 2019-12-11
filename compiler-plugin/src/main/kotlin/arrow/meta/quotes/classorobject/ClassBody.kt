package arrow.meta.quotes.classorobject

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty

class ClassBody(
  override val value: KtClassBody?,
  val companionObjects: ScopedList<KtObjectDeclaration> = ScopedList(value?.allCompanionObjects ?: listOf()),
  val anonymousInitializers: ScopedList<KtAnonymousInitializer> = ScopedList(value?.anonymousInitializers ?: listOf()),
  val danglingAnnotations: ScopedList<KtAnnotationEntry> = ScopedList(value?.danglingAnnotations ?: listOf()),
  val enumEntries: ScopedList<KtEnumEntry> = ScopedList(value?.enumEntries ?: listOf()),
  val functions: ScopedList<KtNamedFunction> = ScopedList(value?.functions ?: listOf()),
  val properties: ScopedList<KtProperty> = ScopedList(value?.properties ?: listOf())
) : Scope<KtClassBody>(value)