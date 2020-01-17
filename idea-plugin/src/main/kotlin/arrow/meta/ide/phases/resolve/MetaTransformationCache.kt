package arrow.meta.ide.phases.resolve

import arrow.meta.quotes.ktFile
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache interface.
 * This simplifies testing and simplifies the implementation of QuoteSystemCache, which was doing
 * too many things at once.
 * This also simplifies thread-safe cache updates, when it's turns out that we need this.
 */
interface MetaTransformationCache {
  /**
   * The number of source files managed by this cache.
   */
  val size: Int

  /**
   * List of packages, which are managed by this cache.
   */
  fun packageList(): List<FqName>

  /**
   * Nullable list of descriptors, which describe elements in package 'name'.
   */
  fun resolved(name: FqName): List<DeclarationDescriptor>?

  /**
   * Returns if the given source file is contained in this cache.
   */
  fun containsSourceFile(source: PsiFile): Boolean

  /**
   * Removes all data from the cache, which belongs to the source file or a transformation of the source file.
   */
  fun removeTransformations(source: KtFile)

  /**
   * Updates the cache with new transformations of the source file.
   * 1. Old elements, which belong to previous transformations of the source file, are removed
   * 2. The new descriptors are added to the package data of the source file.
   */
  fun updateTransformations(source: KtFile, transformedFile: KtFile, descriptors: List<DeclarationDescriptor>)

  /**
   * Clears all data, which is managed by this cache.
   */
  fun clear()
}

class DefaultMetaTransformationCache : MetaTransformationCache {
  // fixme must not be used in production, because caching PsiElement this way is bad
  // fixme cache both per module? modules may define different ktfiles for the same package fqName
  private val transformedFiles = ConcurrentHashMap<KtFile, KtFile>()
  private val resolved = ConcurrentHashMap<FqName, MutableList<DeclarationDescriptor>>()

  override val size: Int
    get() : Int = transformedFiles.size

  override fun resolved(name: FqName): List<DeclarationDescriptor>? {
    // return a copy, we must not return mutable data, which is modified by this cache
    return resolved[name]?.toCollection(mutableListOf())
  }

  override fun packageList(): List<FqName> {
    val packages = LinkedHashSet<FqName>()
    transformedFiles.values.forEach { packages.add(it.packageFqName) }
    return packages.toList()
  }

  override fun containsSourceFile(source: PsiFile): Boolean {
    return transformedFiles.containsKey(source)
  }

  override fun removeTransformations(source: KtFile) {
    val transformedFile = transformedFiles[source] ?: return
    resolved[source.packageFqName]?.removeIf {
      transformedFile == it.ktFile()
    }
  }

  override fun updateTransformations(source: KtFile, transformedFile: KtFile, descriptors: List<DeclarationDescriptor>) {
    removeTransformations(source)

    transformedFiles[source] = transformedFile
    resolved.computeIfAbsent(source.packageFqName) { mutableListOf() }.addAll(descriptors)
  }

  override fun clear() {
    resolved.clear()
    transformedFiles.clear()
  }
}