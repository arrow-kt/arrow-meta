package arrow.meta.ide.phases.resolve

import arrow.meta.quotes.AnalysisDefinition
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * transformed KtFile and containing descriptors
 */
typealias QuotedFile = Pair<KtFile, List<DeclarationDescriptor>>

/**
 * a QuoteCache instance is a record type or data structure over the source files it operates over
 */
interface QuoteCache {

  /**
   * [cache] includes the original files, which are the `keys`, with their transformed files and new descriptors [QuotedFile] `values`.
   */
  val cache: ConcurrentMap<KtFile, QuotedFile>

  /**
   * Clears all data, which is managed by this cache.
   */
  fun clearCache(): Unit =
    cache.clear()

  fun removeQuotedFile(file: KtFile): QuotedFile? =
    cache.remove(file)

  fun update(origin: KtFile, transformed: QuotedFile): Unit {
    cache[origin] = transformed
  }

  fun packages(): List<FqName> =
    cache.keys.filterNotNull().mapNotNull { it.packageFqName }

  /**
   * @param fqName is the package FqName
   */
  fun descriptors(fqName: FqName): List<DeclarationDescriptor> =
    cache.filterKeys { file: KtFile -> file.packageFqName == fqName }.mapNotNull { it.value?.second }.flatten()

  fun descriptors(file: KtFile): List<DeclarationDescriptor> =
    cache[file]?.second ?: emptyList()

  companion object {

    /**
     * @param cache contains files before the quote transformation `keys` and after `value`
     */
    fun default(cache: ConcurrentHashMap<KtFile, QuotedFile> = ConcurrentHashMap()): QuoteCache =
      object : QuoteCache {
        override val cache: ConcurrentMap<KtFile, QuotedFile> = cache
      }
  }
}

/**
 * returns the [DeclarationDescriptor]s of each File
 */
fun KtFile.resolve(facade: ResolutionFacade, resolveMode: BodyResolveMode = BodyResolveMode.PARTIAL): Pair<KtFile, List<DeclarationDescriptor>> =
  this to declarations.mapNotNull { facade.resolveToDescriptor(it, resolveMode) }

/**
 * project level service with quote cache
 */
interface QuoteSystemService {
  /**
   * current project cache
   */
  val cache: QuoteCache

  /**
   * [ExecutorService] is the computational context if [CacheStrategy.backgroundTask] == true
   * @see computeRefreshCache default implementation
   */
  val exec: ExecutorService

  /**
   * transforms all files in the receiver
   * @returns a List of transformed files (OldFile, NewFile)
   * @param extensions registered for quotes
   */
  fun transform(files: List<KtFile>, extensions: List<AnalysisDefinition>): List<Pair<KtFile, KtFile>>

  /**
   * transforms and [files] and repopulates the [cache] based on the [strategy]
   *
   */
  fun refreshCache(files: List<KtFile>, strategy: CacheStrategy = cacheStrategy(resetCache = true, backgroundTask = true)): Unit

  /**
   * refreshes [cache] with an computational context for instance [ExecutorService] if [CacheStrategy.backgroundTask] == true
   */
  fun computeRefreshCache(files: List<KtFile>, strategy: CacheStrategy = cacheStrategy(resetCache = true, backgroundTask = true), refresh: () -> Unit): Unit {
    if (strategy.backgroundTask) exec.submit(refresh) else refresh()
  }
}

/**
 * complements the quote service with a cache strategy
 */
interface CacheStrategy {
  val resetCache: Boolean
  val backgroundTask: Boolean
}

fun cacheStrategy(resetCache: Boolean = true, backgroundTask: Boolean = true): CacheStrategy =
  object : CacheStrategy {
    override val resetCache: Boolean = resetCache
    override val backgroundTask: Boolean = backgroundTask
  }

@Suppress("UNCHECKED_CAST")
fun <F : PsiFile> List<VirtualFile>.files(project: Project): List<F> =
  mapNotNull { PsiManager.getInstance(project).findFile(it) as? F }

fun Project.ktFiles(): List<VirtualFile> =
  FileTypeIndex.getFiles(KotlinFileType.INSTANCE, projectScope()).filterNotNull()

fun VirtualFile.quoteRelevantFile(): Boolean {
  return isValid &&
    this.fileType is KotlinFileType &&
    (isInLocalFileSystem || ApplicationManager.getApplication().isUnitTestMode)
}

/**
 * Collects all Kotlin files of the current project which are source files for Meta transformations.
 */
fun Project.quoteRelevantFiles(): List<KtFile> =
  ktFiles()
    .filter { it.quoteRelevantFile() && it.isInLocalFileSystem }
    .files(this)

interface TestQuoteSystemService {
  val service: QuoteSystemService

  fun reset(): Unit = service.cache.clearCache()
  fun flush(): Any? = service.exec.submit { }.get(5000, TimeUnit.MILLISECONDS)
  fun forceRebuild(project: Project): Unit {
    reset()
    service.refreshCache(project.quoteRelevantFiles())
    flush()
  }
}

fun toTestEnv(service: QuoteSystemService): TestQuoteSystemService =
  object : TestQuoteSystemService {
    override val service: QuoteSystemService = service
  }