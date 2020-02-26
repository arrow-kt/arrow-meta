package arrow.meta.ide.phases.resolve

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap


/**
 * packageFqName and descriptors after quote transformations based on an implicit KtFile
 */
typealias QuoteInfo = Pair<FqName, List<DeclarationDescriptor>>

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

  companion object {
    /**
     * fixme must not be used in production, because caching PsiElement this way is bad
     * fixme cache both per module? modules may define different ktfiles for the same package fqName
     */
    val default: QuoteCache
      get() = object : QuoteCache {

        private val cache = ConcurrentHashMap<KtFile, QuoteInfo>()

        override val size: Int
          get() : Int = cache.size

        override fun descriptors(packageFqName: FqName): List<DeclarationDescriptor> =
          cache.values.filter { it.first == packageFqName }.map { it.second }.flatten()

        override fun packages(): List<FqName> =
          cache.values.map { it.first }.distinct()

        override fun removeQuotedFile(file: KtFile): QuoteInfo? =
          cache.remove(file)

        override fun update(source: KtFile, transformed: QuoteInfo) {
          cache[source] = transformed
        }

        override fun clear(): Unit =
          cache.clear()
      }
  }
}