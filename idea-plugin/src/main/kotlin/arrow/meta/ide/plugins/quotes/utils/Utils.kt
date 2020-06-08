package arrow.meta.ide.plugins.quotes.utils

import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.synthetic.isMetaSynthetic
import arrow.meta.quotes.ktFile
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import arrow.meta.quotes.ktClassOrObject as classOrObject

fun ClassDescriptor.ktClassOrObject(): KtClassOrObject? =
  classOrObject()

fun ClassDescriptor.packageName(): FqName? =
  ktClassOrObject()?.containingKtFile?.packageFqName

fun DeclarationDescriptor.ktFile(): KtFile? =
  ktFile()

internal fun <A> ClassDescriptor.synthetic(f: ClassDescriptor.(packageName: FqName) -> A): A? =
  takeIf { !it.isMetaSynthetic() }?.packageName()?.let { f(it) }

inline fun <reified A> QuoteCache.descriptors(name: FqName): List<A> =
  descriptors(name).filterIsInstance<A>()

internal inline fun
  <A : ClassDescriptor, reified B>
  List<A>.syntheticMembersOf(
  descriptor: ClassDescriptor
): List<B> =
  find { it.fqNameSafe == descriptor.fqNameSafe }
    ?.unsubstitutedMemberScope
    ?.getContributedDescriptors { true }
    .orEmpty()
    .filter { it.isMetaSynthetic() }
    .filterIsInstance<B>()

/**
 * collects synthetic members of type B in [descriptor] of type A in package [name]
 */
internal inline fun <reified A : ClassDescriptor, reified B>
  QuoteCache.syntheticMembersOf(
  name: FqName,
  descriptor: A
): List<B> =
  descriptors<A>(name)
    .syntheticMembersOf(descriptor)