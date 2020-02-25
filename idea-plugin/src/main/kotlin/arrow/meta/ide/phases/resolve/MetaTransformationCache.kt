package arrow.meta.ide.phases.resolve

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap


/**
 * packageFqName and descriptors after quote transformations of a given KtFile
 */
typealias PackageInfo = Pair<FqName, List<DeclarationDescriptor>>

/**
 * Cache interface.
 * This simplifies testing and simplifies the implementation of QuoteSystemCache, which was doing
 * too many things at once.
 * This also simplifies thread-safe cache updates, when it's turns out that we need this.
 */
interface QuoteCache {
  /**
   * The number of source files managed by this cache.
   */
  val size: Int

  /**
   * List of packages, which are managed by this cache.
   */
  fun packages(): List<FqName>

  /**
   * All descriptors of package 'name'.
   */
  fun descriptors(packageFqName: FqName): List<DeclarationDescriptor>

  /**
   * Removes all data from the cache, which belongs to the source file or a transformation of the source file.
   */
  fun removeQuotedFile(file: KtFile): PackageInfo?

  /**
   * Updates the cache with new transformations of the source file.
   * 1. Old elements, which belong to previous transformations of the source file, are removed
   * 2. The new descriptors are added to the package data of the source file.
   */
  fun update(source: KtFile, transformed: PackageInfo)

  /**
   * Clears all data, which is managed by this cache.
   */
  fun clear()
}

class DefaultQuoteCache : QuoteCache {
  // fixme must not be used in production, because caching PsiElement this way is bad
  // fixme cache both per module? modules may define different ktfiles for the same package fqName
  private val transformedFiles = ConcurrentHashMap<KtFile, PackageInfo>()
  //private val resolved = ConcurrentHashMap<FqName, List<DeclarationDescriptor>>()

  override val size: Int
    get() : Int = transformedFiles.size

  override fun descriptors(packageFqName: FqName): List<DeclarationDescriptor> =
    transformedFiles.values.filter { it.first == packageFqName }.map { it.second }.flatten()
  //resolved[packageFqName]?.toList().orEmpty()

  override fun packages(): List<FqName> =
    transformedFiles.values.map { it.first }.distinct()

  override fun removeQuotedFile(file: KtFile): PackageInfo? =
    transformedFiles.remove(file)
    /*transformedFiles.remove(file)?.let { quoted ->
      resolved[file.packageFqName]?.let { descriptors ->
        resolved[file.packageFqName] = descriptors.filterNot { it.ktFile() == quoted }
      }
    } ?: Unit*/

  override fun update(source: KtFile, transformed: PackageInfo) {
    transformedFiles[source] = transformed
    /*removeQuotedFile(source)
    transformedFiles[source] = transformed.first
    resolved.compute(source.packageFqName) { _, list ->
      transformed.second + (list ?: emptyList())
    }*/
  }

  override fun clear() =
    transformedFiles.clear()
}