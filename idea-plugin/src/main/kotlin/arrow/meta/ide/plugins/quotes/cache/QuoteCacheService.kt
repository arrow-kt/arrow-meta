package arrow.meta.ide.plugins.quotes.cache

import arrow.meta.ide.dsl.application.cache.Id
import arrow.meta.ide.dsl.application.cache.PersistentService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import java.util.concurrent.ConcurrentHashMap

/**
 * a QuoteCache implementation as a PersistentService, which stores Quote transformations on disk.
 * This is an initial implementation, as it does not populate the index of other connected projects,
 * and the cache stored on disk get out of sync with external changes to the disk, projects sharing source files or other situations.
 * fixme must not be used in production, because caching PsiElement this way is bad
 * fixme cache both per module? modules may define different ktFiles for the same package fqName
 * TODO: register this via Meta.
 */
@State(name = "QuoteCache", storages = [Storage(StoragePathMacros.CACHE_FILE)])
private object QuoteCacheService : PersistentService<ConcurrentHashMap<KtFile, QuoteInfo>>, QuoteCache {
  override var value: Id<ConcurrentHashMap<KtFile, QuoteInfo>> =
    Id.just(ConcurrentHashMap<KtFile, QuoteInfo>())

  override val size: Int
    get() = value.extract().size

  override fun packages(): List<FqName> =
    value.extract().values.map { it.first }.distinct()

  override fun descriptors(packageFqName: FqName): List<DeclarationDescriptor> =
    value.extract().values.filter { it.first == packageFqName }.map { it.second }.flatten()

  override fun removeQuotedFile(file: KtFile): QuoteInfo? =
    value.extract().remove(file).also { _ ->
      map { // update the cache
        it.apply { remove(file) }
      }
    }

  override fun update(source: KtFile, transformed: QuoteInfo): Unit {
    map {
      it.apply {
        put(source, transformed)
      }
    }
  }

  override fun clear(): Unit {
    map { ConcurrentHashMap<KtFile, QuoteInfo>() }
  }
}