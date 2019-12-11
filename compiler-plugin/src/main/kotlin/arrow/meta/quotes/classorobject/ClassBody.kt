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
  val companionObjects: ScopedList<KtObjectDeclaration> = ScopedList(value?.allCompanionObjects ?: listOf(), separator = "\n"),
  val anonymousInitializers: ScopedList<KtAnonymousInitializer> = ScopedList(value?.anonymousInitializers ?: listOf(), separator = "\n"),
  val danglingAnnotations: ScopedList<KtAnnotationEntry> = ScopedList(value?.danglingAnnotations ?: listOf(), separator = "\n"),
  val enumEntries: ScopedList<KtEnumEntry> = ScopedList(value?.enumEntries ?: listOf(), separator = "\n"),
  val functions: ScopedList<KtNamedFunction> = ScopedList(value?.functions ?: listOf(), separator = "\n"),
  val properties: ScopedList<KtProperty> = ScopedList(value?.properties ?: listOf(), separator = "\n")
) : Scope<KtClassBody>(value)