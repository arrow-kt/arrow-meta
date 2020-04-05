package arrow.meta.ide.plugins.quotes.cache

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile

/**
 * packageFqName and descriptors after quote transformations based on an implicit KtFile.
 */
typealias QuoteInfo = Pair<FqName, List<DeclarationDescriptor>>

/**
 * The QuoteCache provides the following functions to manipulate its underlying data structure,
 * that stores and updates quote transformations.
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
   * Removes the quote information in the cache from a source file and returns it
   */
  fun removeQuotedFile(file: KtFile): QuoteInfo?

  /**
   * Updates the cache with quote transformations of the [source] file.
   */
  fun update(source: KtFile, transformed: QuoteInfo)

  /**
   * Clears cache data
   */
  fun clear()
}